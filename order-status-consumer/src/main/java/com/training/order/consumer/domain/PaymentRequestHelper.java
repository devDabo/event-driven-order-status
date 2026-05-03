package com.training.order.consumer.domain;

import com.training.order.consumer.domain.dto.PaymentRequest;
import com.training.order.consumer.domain.exception.PaymentNotFoundException;
import com.training.order.consumer.domain.mapper.PaymentDataMapper;
import com.training.order.consumer.domain.outbox.scheduler.OrderOutboxHelper;
import com.training.order.consumer.domain.valueobject.PaymentStatus;
import com.training.order.consumer.outbox.OutboxStatus;
import com.training.order.consumer.payment.dataaccess.payment.entity.PaymentEntity;
import com.training.order.consumer.payment.dataaccess.payment.repository.PaymentJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class PaymentRequestHelper {

    private final PaymentDataMapper paymentDataMapper;
    private final PaymentJpaRepository paymentJpaRepository;
    private final OrderOutboxHelper orderOutboxHelper;

    public PaymentRequestHelper(PaymentDataMapper paymentDataMapper,
                                PaymentJpaRepository paymentJpaRepository,
                                OrderOutboxHelper orderOutboxHelper) {
        this.paymentDataMapper = paymentDataMapper;
        this.paymentJpaRepository = paymentJpaRepository;
        this.orderOutboxHelper = orderOutboxHelper;
    }

    @Transactional
    public void persistPayment(PaymentRequest paymentRequest) {
        if (isOutboxMessageProcessedForPayment(paymentRequest, PaymentStatus.COMPLETED)) {
            log.info("An outbox message with saga id: {} is already saved to database!", paymentRequest.getSagaId());
            return;
        }

        log.info("Received payment complete event for order id: {}", paymentRequest.getOrderId());
        PaymentEntity payment = paymentDataMapper.paymentRequestModelToPayment(paymentRequest);
        paymentJpaRepository.save(payment);

        orderOutboxHelper.saveOrderOutboxMessage(paymentDataMapper.paymentEntityToOrderEventPayload(payment, List.of()),
                PaymentStatus.COMPLETED,
                OutboxStatus.STARTED,
                UUID.fromString(paymentRequest.getSagaId()));
    }

    @Transactional
    public void persistCancelPayment(PaymentRequest paymentRequest) {
        if (isOutboxMessageProcessedForPayment(paymentRequest, PaymentStatus.CANCELLED)) {
            log.info("An outbox message with saga id: {} is already saved to database!", paymentRequest.getSagaId());
            return;
        }

        log.info("Received payment rollback event for order id: {}", paymentRequest.getOrderId());
        Optional<PaymentEntity> paymentResponse = paymentJpaRepository.findByOrderId(UUID.fromString(paymentRequest.getOrderId()));
        if (paymentResponse.isEmpty()) {
            log.error("Payment with order id: {} could not be found!", paymentRequest.getOrderId());
            throw new PaymentNotFoundException("Payment with order id: " + paymentRequest.getOrderId() + " could not be found!");
        }
        PaymentEntity payment = paymentResponse.get();
        payment.setStatus(PaymentStatus.CANCELLED);
        paymentJpaRepository.save(payment);

        orderOutboxHelper.saveOrderOutboxMessage(paymentDataMapper.paymentEntityToOrderEventPayload(payment, List.of()),
                PaymentStatus.CANCELLED,
                OutboxStatus.STARTED,
                UUID.fromString(paymentRequest.getSagaId()));
    }

    private boolean isOutboxMessageProcessedForPayment(PaymentRequest paymentRequest,
                                                       PaymentStatus paymentStatus) {
        return orderOutboxHelper.getCompletedOrderOutboxMessageBySagaIdAndPaymentStatus(
                UUID.fromString(paymentRequest.getSagaId()), paymentStatus).isPresent();
    }
}
