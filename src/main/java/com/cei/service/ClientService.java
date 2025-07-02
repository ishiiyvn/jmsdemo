package com.cei.service;

import com.cei.model.Order;

public interface ClientService {
	public void addOrder(Order order);

	public void sendOrderNotification(Order order);
}
