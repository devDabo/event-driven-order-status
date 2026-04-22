package com.training.order.producer;

import org.springframework.boot.SpringApplication;

public class TestOrderStatusProducerApplication {

	public static void main(String[] args) {
		SpringApplication.from(OrderStatusProducerApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
