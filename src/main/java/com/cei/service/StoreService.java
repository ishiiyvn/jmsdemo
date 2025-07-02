package com.cei.service;

import java.util.List;
import java.util.Optional;

import com.cei.model.Order;

public interface StoreService {
	
	void registerOrder(Order order);
	Optional<Order> getReceivedOrder(String id);
	
	void handleOrderNotification(Order order);
	void logOrderEvent(Order order);
	List<String> getNotificationHistory();

}
