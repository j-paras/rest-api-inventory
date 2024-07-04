package com.bootcamp.inventory;

import com.bootcamp.inventory.entity.Inventory;
import com.bootcamp.inventory.repository.InventoryRepository;
import com.bootcamp.inventory.service.InventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
public class InventoryApplicationTests {

	@Mock
	private InventoryRepository inventoryRepository;

	private InventoryService inventoryService;

	private Inventory existingInventory;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);

		existingInventory = new Inventory();
		existingInventory.setId(1L);
		existingInventory.setSku("SKU123");
		existingInventory.setCreatedBy("user1");
		existingInventory.setCost(50.0);
		existingInventory.setSellingPrice(100.0);
		existingInventory.setLocation("Warehouse A");
		existingInventory.setStatus("MODIFIED");
		existingInventory.setUpdatedAt(LocalDateTime.now());
		when(inventoryRepository.findById(1L)).thenReturn(Optional.of(existingInventory));
	}

	@Test
	public void testModifiedStatus_Sold() {
		Inventory updateInventory = new Inventory();
		updateInventory.setId(1L);
		updateInventory.setStatus("SOLD");

		inventoryService.modifiedStatus(updateInventory, existingInventory);

		assertEquals("SOLD", existingInventory.getStatus());
	}

	@Test
	public void testModifiedStatus_NotSold() {
		Inventory updateInventory = new Inventory();
		updateInventory.setId(1L);
		updateInventory.setStatus("something_else");

		inventoryService.modifiedStatus(updateInventory, existingInventory);

		assertEquals("MODIFIED", existingInventory.getStatus());
	}
}
