package com.training.order.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = "com.training.order.consumer.payment.dataaccess")
@EntityScan(basePackages = "com.training.order.consumer.payment.dataaccess")
@SpringBootApplication(scanBasePackages = "com.training.order.consumer")
public class OrderStatusConsumerApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderStatusConsumerApplication.class, args);
	}

}
