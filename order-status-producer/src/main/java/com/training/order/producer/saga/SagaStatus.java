package com.training.order.producer.saga;

public enum SagaStatus {
    STARTED,
    FAILED,
    SUCCEEDED,
    PROCESSING,
    COMPENSATING,
    COMPENSATED
}
