package com.bootcamp.inventory.service;

import com.bootcamp.inventory.configuration.ItemType;
import com.bootcamp.inventory.entity.Inventory;
import com.bootcamp.inventory.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    public ResponseEntity<?> updateInventory(Long id, Inventory updateInventory) {
        try {
            Optional<Inventory> existingInventoryOptional = inventoryRepository.findById(id);
            if (existingInventoryOptional.isPresent()) {
                Inventory existingInventory = existingInventoryOptional.get();
                if (!existingInventory.getCreatedBy().equals(updateInventory.getCreatedBy())) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Created by mismatch. Cannot update inventory created by different user.");
                }
                if (!existingInventory.getSku().equals(updateInventory.getSku())) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("SKU mismatch. Cannot update inventory with different SKU.");
                }
                if (updateInventory.getCost() < 0) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cost cannot be negative");
                }
                if (!isValidItemType(updateInventory.getType())) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid inventory type: " + updateInventory.getType());
                }
                existingInventory.setCost(updateInventory.getCost());
                existingInventory.setSellingPrice(updateInventory.getSellingPrice());
                existingInventory.setLocation(updateInventory.getLocation());
                existingInventory.setUpdatedAt(LocalDateTime.now());
                if (updateInventory.getStatus() != null && updateInventory.getStatus().equalsIgnoreCase("SOLD")) {
                    existingInventory.setStatus("SOLD");
                } else {
                    existingInventory.setStatus("MODIFIED");
                }
                Inventory savedInventory = inventoryRepository.save(existingInventory);
                return ResponseEntity.ok(savedInventory);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update the inventory: " + e.getMessage());
        }
    }
    private boolean isValidItemType(String type) {
        try {
            ItemType.valueOf(type.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}

