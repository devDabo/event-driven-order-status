package com.training.order.producer.domain;

import com.training.order.producer.domain.dto.message.PaymentResponse;
import com.training.order.producer.domain.exception.OrderNotFoundException;
import com.training.order.producer.domain.outbox.model.payment.OrderPaymentOutboxMessage;
import com.training.order.producer.domain.outbox.scheduler.payment.PaymentOutboxHelper;
import com.training.order.producer.domain.valueobject.PaymentStatus;
import com.training.order.producer.entity.Order;
import com.training.order.producer.entity.OrderStatus;
import com.training.order.producer.outbox.OutboxStatus;
import com.training.order.producer.repository.OrderRepository;
import com.training.order.producer.saga.SagaStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class OrderPaymentSaga {

    private static final String UTC = "UTC";

    private final OrderRepository orderRepository;
    private final PaymentOutboxHelper paymentOutboxHelper;

    public OrderPaymentSaga(OrderRepository orderRepository,
                            PaymentOutboxHelper paymentOutboxHelper) {
        this.orderRepository = orderRepository;
        this.paymentOutboxHelper = paymentOutboxHelper;
    }

    @Transactional
    public void process(PaymentResponse paymentResponse) {
        Optional<OrderPaymentOutboxMessage> outboxMessageResponse =
                paymentOutboxHelper.getPaymentOutboxMessageBySagaIdAndSagaStatus(
                        UUID.fromString(paymentResponse.getSagaId()),
                        SagaStatus.STARTED);

        if (outboxMessageResponse.isEmpty()) {
            log.info("Payment response with saga id: {} is already processed", paymentResponse.getSagaId());
            return;
        }

        OrderPaymentOutboxMessage outboxMessage = outboxMessageResponse.get();
        Order order = findOrder(paymentResponse.getOrderId());
        order.setOrderStatus(OrderStatus.PAID);
        order.setUpdatedAt(ZonedDateTime.now(ZoneId.of(UTC)));
        orderRepository.save(order);

        paymentOutboxHelper.save(getUpdatedOutboxMessage(outboxMessage, order.getOrderStatus(), SagaStatus.SUCCEEDED));
        log.info("Order with id: {} is paid", order.getId());
    }

    @Transactional
    public void rollback(PaymentResponse paymentResponse) {
        Optional<OrderPaymentOutboxMessage> outboxMessageResponse =
                paymentOutboxHelper.getPaymentOutboxMessageBySagaIdAndSagaStatus(
                        UUID.fromString(paymentResponse.getSagaId()),
                        getCurrentSagaStatus(paymentResponse.getPaymentStatus()));

        if (outboxMessageResponse.isEmpty()) {
            log.info("Payment response with saga id: {} is already rolled back", paymentResponse.getSagaId());
            return;
        }

        OrderPaymentOutboxMessage outboxMessage = outboxMessageResponse.get();
        Order order = findOrder(paymentResponse.getOrderId());
        order.setOrderStatus(OrderStatus.CANCELLED);
        order.setUpdatedAt(ZonedDateTime.now(ZoneId.of(UTC)));
        orderRepository.save(order);

        paymentOutboxHelper.save(getUpdatedOutboxMessage(outboxMessage, order.getOrderStatus(),
                getTargetSagaStatus(paymentResponse.getPaymentStatus())));
        log.info("Order with id: {} is cancelled", order.getId());
    }

    private Order findOrder(String orderId) {
        return orderRepository.findById(UUID.fromString(orderId))
                .orElseThrow(() -> new OrderNotFoundException("Order with id " + orderId + " could not be found"));
    }

    private OrderPaymentOutboxMessage getUpdatedOutboxMessage(OrderPaymentOutboxMessage outboxMessage,
                                                              OrderStatus orderStatus,
                                                              SagaStatus sagaStatus) {
        outboxMessage.setProcessedAt(ZonedDateTime.now(ZoneId.of(UTC)));
        outboxMessage.setOrderStatus(orderStatus);
        outboxMessage.setSagaStatus(sagaStatus);
        outboxMessage.setOutboxStatus(OutboxStatus.COMPLETED);
        return outboxMessage;
    }

    private SagaStatus[] getCurrentSagaStatus(PaymentStatus paymentStatus) {
        return switch (paymentStatus) {
            case COMPLETED -> new SagaStatus[]{SagaStatus.STARTED};
            case CANCELLED -> new SagaStatus[]{SagaStatus.COMPENSATING};
            case FAILED -> new SagaStatus[]{SagaStatus.STARTED};
        };
    }

    private SagaStatus getTargetSagaStatus(PaymentStatus paymentStatus) {
        return switch (paymentStatus) {
            case COMPLETED -> SagaStatus.SUCCEEDED;
            case CANCELLED -> SagaStatus.COMPENSATED;
            case FAILED -> SagaStatus.FAILED;
        };
    }
}
