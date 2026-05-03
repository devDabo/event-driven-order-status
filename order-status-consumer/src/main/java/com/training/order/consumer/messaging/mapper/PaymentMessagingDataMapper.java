package com.training.order.consumer.messaging.mapper;

import com.training.order.consumer.domain.dto.PaymentRequest;
import com.training.order.consumer.domain.event.payload.OrderPaymentEventPayload;
import com.training.order.consumer.domain.valueobject.PaymentOrderStatus;
import debezium.order.payment_outbox.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class PaymentMessagingDataMapper {

    public PaymentRequest paymentRequestAvroModelToPaymentRequest(OrderPaymentEventPayload orderPaymentEventPayload,
                                                                  Value paymentRequestAvroModel) {
        return PaymentRequest.builder()
                .id(paymentRequestAvroModel.getId())
                .sagaId(paymentRequestAvroModel.getSagaId())
                .customerId(orderPaymentEventPayload.getCustomerId())
                .orderId(orderPaymentEventPayload.getOrderId())
                .price(orderPaymentEventPayload.getPrice())
                .createdAt(Instant.parse(paymentRequestAvroModel.getCreatedAt()))
                .paymentOrderStatus(PaymentOrderStatus.valueOf(orderPaymentEventPayload.getPaymentOrderStatus()))
                .build();
    }
}
