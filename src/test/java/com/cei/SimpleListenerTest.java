package com.cei;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.awaitility.Awaitility.await;


import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import com.cei.model.Order;
import com.cei.service.ClientService;
import com.cei.service.StoreService;

@SpringBootTest
public class SimpleListenerTest {
	
	@Autowired
	private ClientService clientService;
	
	@Autowired
	private StoreService storeService;
	
	@Test
	public void sendSimpleMessage() {
		System.out.println("Sending order to the queue...");
		Order order = new Order("order1");
		clientService.addOrder(order);
		
		await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
			Optional<Order> storedOrder = storeService.getReceivedOrder("order1");
			assertTrue(storedOrder.isPresent());
			assertEquals("order1", storedOrder.get().getId());

			boolean notificationFound = storeService.getNotificationHistory().stream()
				.anyMatch(notification -> notification.contains("order1"));
			assertTrue(notificationFound, "Notification for order1 should be present in history");
		});
	}
	
}
