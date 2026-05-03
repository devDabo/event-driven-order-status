package com.training.order.consumer.messaging;

public enum DebeziumOp {

    CREATE("c"), UPDATE("u"), DELETE("d");

    private final String value;

    DebeziumOp(String val) {
        this.value = val;
    }

    public String getValue() {
        return value;
    }
}
