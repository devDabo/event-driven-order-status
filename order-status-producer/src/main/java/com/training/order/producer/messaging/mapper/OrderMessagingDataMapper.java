package com.training.order.producer.messaging.mapper;

import com.training.order.producer.domain.dto.message.PaymentResponse;
import com.training.order.producer.domain.event.payload.PaymentOrderEventPayload;
import com.training.order.producer.domain.valueobject.PaymentStatus;
import org.apache.avro.generic.GenericRecord;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class OrderMessagingDataMapper {

    public PaymentResponse paymentResponseRecordToPaymentResponse(PaymentOrderEventPayload payload,
                                                                  GenericRecord paymentResponseRecord) {
        return PaymentResponse.builder()
                .id(paymentResponseRecord.get("id").toString())
                .sagaId(paymentResponseRecord.get("saga_id").toString())
                .paymentId(payload.getPaymentId())
                .customerId(payload.getCustomerId())
                .orderId(payload.getOrderId())
                .price(payload.getPrice())
                .createdAt(Instant.parse(paymentResponseRecord.get("created_at").toString()))
                .paymentStatus(PaymentStatus.valueOf(payload.getPaymentStatus()))
                .failureMessages(payload.getFailureMessages())
                .build();
    }
}
