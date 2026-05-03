package com.training.order.consumer.domain.mapper;

import com.training.order.consumer.domain.dto.PaymentRequest;
import com.training.order.consumer.domain.outbox.model.OrderEventPayload;
import com.training.order.consumer.domain.valueobject.PaymentStatus;
import com.training.order.consumer.payment.dataaccess.payment.entity.PaymentEntity;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Component
public class PaymentDataMapper {

    private static final String UTC = "UTC";

    public PaymentEntity paymentRequestModelToPayment(PaymentRequest paymentRequest) {
        return PaymentEntity.builder()
                .id(UUID.randomUUID())
                .orderId(UUID.fromString(paymentRequest.getOrderId()))
                .customerId(UUID.fromString(paymentRequest.getCustomerId()))
                .price(paymentRequest.getPrice())
                .createdAt(ZonedDateTime.ofInstant(paymentRequest.getCreatedAt(), ZoneId.of(UTC)))
                .status(PaymentStatus.COMPLETED)
                .build();
    }

    public OrderEventPayload paymentEntityToOrderEventPayload(PaymentEntity paymentEntity, List<String> failureMessages) {
        return OrderEventPayload.builder()
                .paymentId(paymentEntity.getId().toString())
                .customerId(paymentEntity.getCustomerId().toString())
                .orderId(paymentEntity.getOrderId().toString())
                .price(paymentEntity.getPrice())
                .createdAt(paymentEntity.getCreatedAt())
                .paymentStatus(paymentEntity.getStatus().name())
                .failureMessages(failureMessages)
                .build();
    }
}
