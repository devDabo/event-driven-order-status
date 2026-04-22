package com.training.order.producer.service.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.training.order.producer.entity.Order;
import com.training.order.producer.service.event.dto.OrderStatusUpdateEventDto;
import org.springframework.stereotype.Service;
@Service
public class OrderStatusUpdateEventService {

    private final SnsService snsService;
    private final ObjectMapper objectMapper;
    private final String topicArn = "arn:aws:sns:us-east-1:000000000000:local-events-dispatch";

    public OrderStatusUpdateEventService(SnsService snsService, ObjectMapper objectMapper) {
        this.snsService = snsService;
        this.objectMapper = objectMapper;
    }

    public void publishEvent(Order order) {
        OrderStatusUpdateEventDto orderStatusUpdateEventDto = new OrderStatusUpdateEventDto(
                order.getId(),
                order.getStatus()
        );
        try {
            String jsonPayload = objectMapper.writeValueAsString(orderStatusUpdateEventDto);
            snsService.publish(topicArn, jsonPayload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
