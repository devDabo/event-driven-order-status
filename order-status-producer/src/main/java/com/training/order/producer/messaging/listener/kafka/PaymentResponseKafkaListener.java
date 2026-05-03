package com.training.order.producer.messaging.listener.kafka;

import com.training.order.producer.domain.dto.message.PaymentResponse;
import com.training.order.producer.domain.event.payload.PaymentOrderEventPayload;
import com.training.order.producer.domain.exception.OrderNotFoundException;
import com.training.order.producer.domain.ports.input.message.listener.payment.PaymentResponseMessageListener;
import com.training.order.producer.domain.valueobject.PaymentStatus;
import com.training.order.producer.kafka.KafkaMessageHelper;
import com.training.order.producer.kafka.consumer.KafkaConsumer;
import com.training.order.producer.messaging.DebeziumOp;
import com.training.order.producer.messaging.mapper.OrderMessagingDataMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericRecord;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class PaymentResponseKafkaListener implements KafkaConsumer<GenericRecord> {

    private final PaymentResponseMessageListener paymentResponseMessageListener;
    private final OrderMessagingDataMapper orderMessagingDataMapper;
    private final KafkaMessageHelper kafkaMessageHelper;

    public PaymentResponseKafkaListener(PaymentResponseMessageListener paymentResponseMessageListener,
                                        OrderMessagingDataMapper orderMessagingDataMapper,
                                        KafkaMessageHelper kafkaMessageHelper) {
        this.paymentResponseMessageListener = paymentResponseMessageListener;
        this.orderMessagingDataMapper = orderMessagingDataMapper;
        this.kafkaMessageHelper = kafkaMessageHelper;
    }

    @Override
    @KafkaListener(id = "${kafka-consumer-config.payment-response-consumer-group-id}",
            topics = "${producer-service.payment-response-topic-name}")
    public void receive(@Payload List<GenericRecord> messages,
                        @Header(KafkaHeaders.RECEIVED_KEY) List<String> keys,
                        @Header(KafkaHeaders.RECEIVED_PARTITION) List<Integer> partitions,
                        @Header(KafkaHeaders.OFFSET) List<Long> offsets) {
        log.info("{} number of payment responses received!",
                messages.stream().filter(this::isCreateEvent).toList().size());

        messages.forEach(message -> {
            if (!isCreateEvent(message)) {
                return;
            }

            log.info("Incoming message in PaymentResponseKafkaListener: {}", message);
            GenericRecord paymentResponseRecord = (GenericRecord) message.get("after");
            PaymentOrderEventPayload paymentOrderEventPayload = kafkaMessageHelper.getOrderEventPayload(
                    paymentResponseRecord.get("payload").toString(), PaymentOrderEventPayload.class);
            PaymentResponse paymentResponse = orderMessagingDataMapper
                    .paymentResponseRecordToPaymentResponse(paymentOrderEventPayload, paymentResponseRecord);

            try {
                if (PaymentStatus.COMPLETED == paymentResponse.getPaymentStatus()) {
                    log.info("Processing successful payment for order id: {}", paymentResponse.getOrderId());
                    paymentResponseMessageListener.paymentCompleted(paymentResponse);
                } else {
                    log.info("Processing unsuccessful payment for order id: {}", paymentResponse.getOrderId());
                    paymentResponseMessageListener.paymentCancelled(paymentResponse);
                }
            } catch (OptimisticLockingFailureException e) {
                log.error("Caught optimistic locking exception for order id: {}", paymentResponse.getOrderId());
            } catch (OrderNotFoundException e) {
                log.error("No order found for order id: {}", paymentResponse.getOrderId());
            }
        });
    }

    private boolean isCreateEvent(GenericRecord message) {
        return message.get("before") == null && DebeziumOp.CREATE.getValue().equals(message.get("op").toString());
    }
}
