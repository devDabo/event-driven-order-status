package com.training.order.consumer;

import org.springframework.boot.SpringApplication;

public class TestOrderStatusConsumerApplication {

	public static void main(String[] args) {
		SpringApplication.from(OrderStatusConsumerApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
