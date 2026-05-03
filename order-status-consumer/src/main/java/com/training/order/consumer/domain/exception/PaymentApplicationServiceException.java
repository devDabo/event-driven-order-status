package com.training.order.consumer.domain.exception;

public class PaymentApplicationServiceException extends RuntimeException {

    public PaymentApplicationServiceException(String message) {
        super(message);
    }

    public PaymentApplicationServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
