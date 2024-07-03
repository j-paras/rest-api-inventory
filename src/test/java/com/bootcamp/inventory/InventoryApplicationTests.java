package com.bootcamp.inventory;

import com.bootcamp.inventory.entity.Inventory;
import com.bootcamp.inventory.repository.InventoryRepository;
import com.bootcamp.inventory.service.InventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class InventoryApplicationTests {

	private InventoryRepository inventoryRepository;

	private InventoryService inventoryService;

	private Inventory existingInventory;

	@Test
	void contextLoads() {
	}

	@BeforeEach
	public void setup(){
		existingInventory = new Inventory();
		existingInventory.setId(1L);
		existingInventory.setSku("SKU123");
		existingInventory.setCreatedBy("user1");
		existingInventory.setCost(50.0);
		existingInventory.setSellingPrice(100.0);
		existingInventory.setLocation("Warehouse A");
		existingInventory.setStatus("MODIFIED");
		existingInventory.setUpdatedAt(LocalDateTime.now());
	}
}
