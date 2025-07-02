package com.cei.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.cei.model.Order;

@Service
public class StoreServiceImpl implements StoreService {
	
	private final List<Order> receiveOrders = new ArrayList<>();
	
	private final List<String> notificationHistory = new ArrayList<>();
	
	@Override
	public void registerOrder(Order order) {
		this.receiveOrders.add(order);
	}
	
	
	@Override
	public Optional<Order> getReceivedOrder(String id) {
		return receiveOrders.stream().
				filter(o -> o.getId().equals(id)).findFirst();
	}
	
	@Override
	public void handleOrderNotification(Order order) {
		String notification = "Order with ID: " + order.getId() + " has been processed.";
		this.notificationHistory.add(notification);
	}
	
	@Override
	public void logOrderEvent(Order order) {
		System.out.println("Order with ID: " + order.getId() + " has been logged.");
	}
	
	@Override
	public List<String> getNotificationHistory() {
		return new ArrayList<>(notificationHistory);
	}
}