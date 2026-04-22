package com.training.order.producer.service.event.dto;

import com.training.order.producer.entity.OrderStatus;

import java.util.UUID;

public record OrderStatusUpdateEventDto (
    String eventName,
    UUID id,
    OrderStatus orderStatus
) {
    public OrderStatusUpdateEventDto(UUID id, OrderStatus orderStatus) {
        this("order_status_update", id, orderStatus);
    }
}
