package com.training.order.producer.api;

import com.training.order.producer.api.dto.CreateOrderRequest;
import com.training.order.producer.api.dto.OrderResponse;
import com.training.order.producer.entity.Order;
import com.training.order.producer.service.track.OrderTrackerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderTrackerService orderTrackerService;

    public OrderController(OrderTrackerService orderTrackerService) {
        this.orderTrackerService = orderTrackerService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder(@Valid @RequestBody CreateOrderRequest request) {
        return OrderResponse.from(orderTrackerService.createOrder(request));
    }

    @PatchMapping("/{orderId}/cancel")
    public OrderResponse cancelOrder(@PathVariable UUID orderId) {
        return OrderResponse.from(orderTrackerService.cancelOrder(orderId));
    }

    @GetMapping("/{orderId}")
    public OrderResponse getOrder(@PathVariable UUID orderId) {
        Order order = orderTrackerService.getOrder(orderId);
        return OrderResponse.from(order);
    }
}
