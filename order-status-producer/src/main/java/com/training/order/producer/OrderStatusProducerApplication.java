package com.training.order.producer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@SpringBootApplication
public class OrderStatusProducerApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderStatusProducerApplication.class, args);
	}

}
