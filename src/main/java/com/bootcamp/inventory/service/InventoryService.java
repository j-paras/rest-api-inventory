package com.bootcamp.inventory.service;

import com.bootcamp.inventory.configuration.ItemType;
import com.bootcamp.inventory.controller.dto.PatchInventoryRequest;
import com.bootcamp.inventory.entity.Inventory;
import com.bootcamp.inventory.repository.InventoryRepository;
import com.bootcamp.inventory.service.validator.CreateInventoryRequestValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class InventoryService {
    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private CreateInventoryRequestValidator createInventoryRequestValidator;

    public List<Inventory> getAll(int pageNumber, int pageSize) {
        validatePage(pageNumber, pageSize);
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Page<Inventory> page = inventoryRepository.findAll(pageable);
            return page.getContent();
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch all inventory:" + e.getMessage());
        }
    }

    private static void validatePage(int pageNumber, int pageSize) {
        if (pageNumber < 0 || pageSize <= 0) {
            throw new IllegalArgumentException("Page number and page size must be positive integers.");
        }
    }

    public Optional<Inventory> getById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID must be a positive non-zero integer.");
        }
        try {
            return inventoryRepository.findById(id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch inventory by id:" + e.getMessage());
        }
    }

    public Inventory save(Inventory inventory) throws Exception {
        if(!validateInventory(inventory)) {
            throw new Exception("Invalid Inventory Request");
        };
//        Inventory inventory = CreateNewInventory(createInventoryRequest);
        try {
//            setDefaultValues(inventory);
            return inventoryRepository.save(inventory);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save the inventory: " + e.getMessage());
        }
    }

    private boolean validateInventory(Inventory inventory) throws Exception {
        return createInventoryRequestValidator.isValid(inventory);
    }

    private void setDefaultValues(Inventory inventory) {
        inventory.setStatus("CREATED");
        inventory.setCreatedAt(LocalDateTime.now());
        inventory.setUpdatedAt(LocalDateTime.now());
        inventory.setCreatedBy("admin");
    }

    public ResponseEntity<?> updateInventory(Long id, Inventory updateInventory) {
        try {
            Optional<Inventory> existingInventoryOptional = inventoryRepository.findById(id);
            return fetchExistingInventoryAndUpdate(updateInventory, existingInventoryOptional);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update the inventory: " + e.getMessage());
        }
    }

    private ResponseEntity<?> fetchExistingInventoryAndUpdate(Inventory updateInventory, Optional<Inventory> existingInventoryOptional) {
        if (existingInventoryOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Inventory existingInventory = existingInventoryOptional.get();
        ResponseEntity<String> BAD_REQUEST = validationCheck(updateInventory, existingInventory);
        if (BAD_REQUEST != null) return BAD_REQUEST;
        return updateAndModifiedStatus(updateInventory, existingInventory);
    }

    private ResponseEntity<Inventory> updateAndModifiedStatus(Inventory updateInventory, Inventory existingInventory) {
        updateData(updateInventory, existingInventory);
        modifiedStatus(updateInventory, existingInventory);
        Inventory savedInventory = inventoryRepository.save(existingInventory);
        return ResponseEntity.ok(savedInventory);
    }

    public static void modifiedStatus(Inventory updateInventory, Inventory existingInventory) {
        if (updateInventory.getStatus() != null && updateInventory.getStatus().equalsIgnoreCase("SOLD")) {
            existingInventory.setStatus("SOLD");
            return;
        }
        existingInventory.setStatus("MODIFIED");
    }

    private static void updateData(Inventory updateInventory, Inventory existingInventory) {
        existingInventory.setCost(updateInventory.getCost());
        existingInventory.setSellingPrice(updateInventory.getSellingPrice());
        existingInventory.setLocation(updateInventory.getLocation());
        existingInventory.setUpdatedAt(LocalDateTime.now());
    }

    private ResponseEntity<String> validationCheck(Inventory updateInventory, Inventory existingInventory) {
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
        return null;
    }

    private boolean isValidItemType(String type) {
        try {
            ItemType.valueOf(type.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public Inventory update(Long id, PatchInventoryRequest patchInventoryRequest) {
        Inventory inventory = inventoryRepository.getById(id);
        inventory.setCost(patchInventoryRequest.price);
        inventory.setStatus(patchInventoryRequest.status);
        inventoryRepository.save(inventory);
    }
}

