package com.cei.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.cei.model.Order;

@Service
public class ClientServiceImpl implements ClientService {

	@Autowired
	@Qualifier("queueJmsTemplate")
	private JmsTemplate queueJmsTemplate;

	@Autowired
	@Qualifier("topicJmsTemplate")
	private JmsTemplate topicJmsTemplate;

	@Autowired
	private ObjectMapper objectMapper;
	
	
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
			topicJmsTemplate.convertAndSend("simple.topic", json, message -> {
				message.setStringProperty("type", "NOTIFICATION");
				return message;
			});
		} catch (Exception e) {
			System.err.println("=== ERROR SENDING SUBSCRIPTION TO TOPIC ===");
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	
	@Override
	public void sendOrderAdd(Order order) {
		try {
			String json = objectMapper.writeValueAsString(order);
			topicJmsTemplate.convertAndSend("simple.topic", json, message -> {
				message.setStringProperty("type", "ADD");
				return message;
			});
		} catch (Exception e) {
			System.err.println("=== ERROR SENDING ADD TO TOPIC ===");
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}