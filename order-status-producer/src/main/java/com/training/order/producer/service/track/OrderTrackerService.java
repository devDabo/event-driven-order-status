package com.training.order.producer.service.track;

import com.training.order.producer.api.dto.CreateOrderRequest;
import com.training.order.producer.domain.event.payload.OrderPaymentEventPayload;
import com.training.order.producer.domain.outbox.scheduler.payment.PaymentOutboxHelper;
import com.training.order.producer.domain.valueobject.PaymentOrderStatus;
import com.training.order.producer.entity.Order;
import com.training.order.producer.entity.OrderStatus;
import com.training.order.producer.outbox.OutboxStatus;
import com.training.order.producer.repository.OrderRepository;
import com.training.order.producer.saga.SagaStatus;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class OrderTrackerService {

    private static final String UTC = "UTC";

    private final OrderRepository orderRepository;
    private final PaymentOutboxHelper paymentOutboxHelper;

    public OrderTrackerService(OrderRepository orderRepository,
                               PaymentOutboxHelper paymentOutboxHelper) {
        this.orderRepository = orderRepository;
        this.paymentOutboxHelper = paymentOutboxHelper;
    }

    @Transactional
    public Order createOrder(CreateOrderRequest request) {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of(UTC));
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setSagaId(UUID.randomUUID());
        order.setCustomerId(request.customerId());
        order.setPrice(request.price());
        order.setOrderStatus(OrderStatus.PENDING);
        order.setCreatedAt(now);
        order.setUpdatedAt(now);
        Order persistedOrder = orderRepository.save(order);

        paymentOutboxHelper.savePaymentOutboxMessage(createOrderPaymentEventPayload(persistedOrder, PaymentOrderStatus.PENDING),
                persistedOrder.getOrderStatus(),
                SagaStatus.STARTED,
                OutboxStatus.STARTED,
                persistedOrder.getSagaId());
        return persistedOrder;
    }

    @Transactional
    public Order cancelOrder(UUID orderId) {
        Order order = getOrder(orderId);
        order.setOrderStatus(OrderStatus.CANCELLING);
        order.setUpdatedAt(ZonedDateTime.now(ZoneId.of(UTC)));
        Order persistedOrder = orderRepository.save(order);

        paymentOutboxHelper.savePaymentOutboxMessage(createOrderPaymentEventPayload(persistedOrder, PaymentOrderStatus.CANCELLED),
                persistedOrder.getOrderStatus(),
                SagaStatus.COMPENSATING,
                OutboxStatus.STARTED,
                persistedOrder.getSagaId());
        return persistedOrder;
    }

    @Transactional
    public Order getOrder(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Order not found: " + orderId));
    }

    private OrderPaymentEventPayload createOrderPaymentEventPayload(Order order, PaymentOrderStatus paymentOrderStatus) {
        return OrderPaymentEventPayload.builder()
                .id(UUID.randomUUID().toString())
                .sagaId(order.getSagaId().toString())
                .orderId(order.getId().toString())
                .customerId(order.getCustomerId().toString())
                .price(order.getPrice())
                .createdAt(order.getCreatedAt())
                .paymentOrderStatus(paymentOrderStatus.name())
                .build();
    }
}
