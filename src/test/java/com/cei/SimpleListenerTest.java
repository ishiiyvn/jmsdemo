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
	public void testQueueMessageFlow() throws Exception {
		Order order = new Order("order1");
		clientService.addOrder(order);

		await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
			Optional<Order> storedOrder = storeService.getReceivedOrder("order1");
			assertTrue(storedOrder.isPresent());
			assertEquals("order1", storedOrder.get().getId());
		});
	}

	@Test
	public void testTopicSubscriptions() throws Exception {
		Order notificationOrder = new Order("notificationOrder");
		Order addOrder = new Order("addOrder");

		clientService.sendOrderNotification(notificationOrder);
		clientService.sendOrderAdd(addOrder);

		await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
			boolean notificationFound = storeService.getNotificationHistory().stream()
				.anyMatch(notification -> notification.contains("notificationOrder"));
			assertTrue(notificationFound, "Notification for notificationOrder should be present in history");

			boolean addFound = storeService.getAddHistory().stream()
				.anyMatch(add -> add.contains("addOrder"));
			assertTrue(addFound, "Add for addOrder should be present in add history");
		});
	}
}
