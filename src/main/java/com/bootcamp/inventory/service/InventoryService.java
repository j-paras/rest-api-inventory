package com.bootcamp.inventory.service;

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
                if (updateInventory.getCost() < 0) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cost cannot be negative");
                }
                existingInventory.setCost(updateInventory.getCost());
                existingInventory.setSellingPrice(updateInventory.getSellingPrice());
                existingInventory.setLocation(updateInventory.getLocation());
                existingInventory.setUpdatedAt(LocalDateTime.now());
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
}

