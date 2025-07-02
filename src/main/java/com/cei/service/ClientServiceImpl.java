package com.cei.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.cei.model.Order;

@Service
public class ClientServiceImpl implements ClientService {

	private final JmsTemplate queueJmsTemplate;
	private final JmsTemplate topicJmsTemplate;
	private final ObjectMapper objectMapper = new ObjectMapper();
	
	
	public ClientServiceImpl(
			@Qualifier("queueJmsTemplate") JmsTemplate queueJmsTemplate,
			@Qualifier("topicJmsTemplate") JmsTemplate topicJmsTemplate) {
		this.queueJmsTemplate = queueJmsTemplate;
		this.topicJmsTemplate = topicJmsTemplate;
	}
	
	
	@Override
	public void addOrder(Order order) {
		try {
			System.out.println("=== SENDING MESSAGE TO QUEUE ===");
			System.out.println("Order to send: " + order);
			String json = objectMapper.writeValueAsString(order);
			System.out.println("JSON to send: " + json);
			queueJmsTemplate.convertAndSend("simple.queue", json);
			System.out.println("Message sent to queue successfully!");
		} catch (Exception e) {
			System.err.println("=== ERROR SENDING MESSAGE TO QUEUE ===");
			System.err.println("Exception: " + e.getClass().getName());
			System.err.println("Message: " + e.getMessage());
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Override
	public void sendOrderNotification(Order order) {
		try {
			String json = objectMapper.writeValueAsString(order);
			topicJmsTemplate.convertAndSend("simple.topic", json);
			System.out.println("Notification sent to topic: " + json);
		} catch (Exception e) {
			System.err.println("=== ERROR SENDING NOTIFICATION TO TOPIC ===");
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}