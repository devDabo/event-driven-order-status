package com.training.order.producer.api.dto;

import com.training.order.producer.entity.Order;
import com.training.order.producer.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        UUID sagaId,
        UUID customerId,
        BigDecimal price,
        OrderStatus orderStatus,
        ZonedDateTime createdAt,
        ZonedDateTime updatedAt
) {
    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getSagaId(),
                order.getCustomerId(),
                order.getPrice(),
                order.getOrderStatus(),
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }
}
