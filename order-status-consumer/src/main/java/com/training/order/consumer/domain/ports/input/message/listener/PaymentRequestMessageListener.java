package com.training.order.consumer.domain.ports.input.message.listener;

import com.training.order.consumer.domain.dto.PaymentRequest;

public interface PaymentRequestMessageListener {

    void completePayment(PaymentRequest paymentRequest);

    void cancelPayment(PaymentRequest paymentRequest);
}
