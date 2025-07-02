package com.cei.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.cei.model.Order;
import com.cei.service.StoreService;
import com.cei.service.ClientService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class SimpleListener {
	
	@Autowired
	private StoreService storeService;

	@Autowired
	private ClientService clientService;

	@Autowired
	private ObjectMapper objectMapper;

	
	@JmsListener(
		destination = "simple.queue",
		containerFactory = "queueListenerContainerFactory"
	)
	public void processOrderMessage(String orderJson) {
		System.out.println("=== LISTENER RECEIVED MESSAGE ===");
		System.out.println("Raw message: " + orderJson);
		try {
			Order order = objectMapper.readValue(orderJson, Order.class);
			System.out.println("Parsed order: " + order);
			System.out.println("Order ID: " + order.getId());
			
			storeService.registerOrder(order);
			System.out.println("Order registered successfully");
			
			clientService.sendOrderNotification(order);
			System.out.println("Notification sent to topic after order processing");
		} catch (Exception e) {
			System.err.println("=== ERROR IN LISTENER ===");
			System.err.println("Exception type: " + e.getClass().getName());
			System.err.println("Exception message: " + e.getMessage());
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	
	@JmsListener(
		destination = "simple.topic",
		containerFactory = "topicListenerContainerFactory",
		selector = "type = 'NOTIFICATION'"
	)
	public void processNotificationSubscription(String orderJson) {
		try {
			Order order = objectMapper.readValue(orderJson, Order.class);
			System.out.println("=== TOPIC LISTENER RECEIVED (NOTIFICATION) ===");
			System.out.println("Order: " + order);
			
			storeService.handleOrderNotification(order);
			storeService.logOrderEvent(order);
			
			System.out.println("Topic processing completed");
		} catch (Exception e) {
			System.err.println("=== ERROR IN TOPIC LISTENER ===");
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@JmsListener(
		destination = "simple.topic",
		containerFactory = "topicListenerContainerFactory",
		selector = "type = 'ADD'"
	)
	public void processAddSubscription(String orderJson) {
		try {
			Order order = objectMapper.readValue(orderJson, Order.class);
			System.out.println("=== TOPIC LISTENER RECEIVED (ADD) ===");
			System.out.println("Order: " + order);
			storeService.handleOrderAdd(order);
			storeService.logAddEvent(order);
		} catch (Exception e) {
			System.err.println("=== ERROR IN TOPIC LISTENER (ADD) ===");
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}