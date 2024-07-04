package com.bootcamp.inventory.service;

import com.bootcamp.inventory.configuration.ItemType;
import com.bootcamp.inventory.entity.Inventory;
import com.bootcamp.inventory.repository.InventoryRepository;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    public List<Inventory> getAllInventory(int pageNumber, int pageSize){
        if (pageNumber < 0 || pageSize <= 0) {
            throw new IllegalArgumentException("Page number and page size must be positive integers.");
        }
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Page<Inventory> page = inventoryRepository.findAll(pageable);
            return page.getContent();
        }catch (Exception e){
            throw new RuntimeException("Failed to fetch all inventory:" + e.getMessage());
        }
    }
    public Optional<Inventory> getInventoryById(Long id){
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID must be a positive non-zero integer.");
        }
        try{
            return inventoryRepository.findById(id);
        }catch (Exception e){
            throw new RuntimeException("Failed to fetch inventory by id:" + e.getMessage());
        }
    }

    public Inventory saveInventory(Inventory inventory) {
        try {
            validateInventory(inventory);
            setDefaultValues(inventory);
            return inventoryRepository.save(inventory);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save the inventory: " + e.getMessage());
        }
    }

    private void validateInventory(Inventory inventory) {
        if (!typeIsValid(inventory.getType())) {
            throw new Error("Invalid inventory type");
        }
        if (!locationIsValid(inventory.getLocation())) {
            throw new Error("Invalid inventory location");
        }
        if (!attributesIsValid(inventory.getType(), inventory.getAttributes())) {
            throw new Error("Invalid inventory attributes");
        }
        if (!priceIsValid(inventory.getCost())) {
            throw new Error("Invalid inventory cost price");
        }
        if (!priceIsValid(inventory.getSellingPrice())) {
            throw new Error("Invalid selling price");
        }
    }

    private void setDefaultValues(Inventory inventory) {
        inventory.setStatus("CREATED");
        inventory.setCreatedAt(LocalDateTime.now());
        inventory.setUpdatedAt(LocalDateTime.now());
        inventory.setCreatedBy("admin");
    }

    private boolean typeIsValid(String type){
        if(type.isEmpty()){
            return false;
        }
        for(ItemType itemType : ItemType.values()){
            if(type.toUpperCase().equals(itemType.name())){
                return true;
            }
        }
        return false;

    }
    private boolean locationIsValid(String location) {
        if (location == null || location.isEmpty()) {
            return false;
        }
        String pattern = "^.*$";
        return location.matches(pattern);
    }
    private boolean priceIsValid(double price) {
        if (price < 0.0 || price > 1000000.0) {
            return false;
        }
        return Math.round(price * 100.0) / 100.0 == price;
    }
    private boolean attributesIsValid(String type, JsonNode attributes) {
        switch (type) {
            case "CAR":
            case "BIKE":
                return validateVehicleAttributes(attributes);
            case "MOBILE":
                return validateMobileAttributes(attributes);
            default:
                return false;
        }
    }

    private boolean validateVehicleAttributes(JsonNode attributes) {
        String vin = attributes.get("vin") != null ? attributes.get("vin").asText() : "";
        String brand = attributes.get("brand") != null ? attributes.get("brand").asText() : "";
        String model = attributes.get("model") != null ? attributes.get("model").asText() : "";
        String yearStr = attributes.get("year") != null ? attributes.get("year").asText() : "";
        int year;
        try {
            year = Integer.parseInt(yearStr);
        } catch (NumberFormatException e) {
            return false;
        }
        return !vin.isEmpty() && !brand.isEmpty() && !model.isEmpty() && isValidYear(year, 2015);
    }

    private boolean validateMobileAttributes(JsonNode attributes) {
        String imei = attributes.get("imei") != null ? attributes.get("imei").asText() : "";
        String brand = attributes.get("brand") != null ? attributes.get("brand").asText() : "";
        String model = attributes.get("model") != null ? attributes.get("model").asText() : "";
        int year = attributes.get("year") != null ? attributes.get("year").asInt() : 0;
        return !imei.isEmpty() && !brand.isEmpty() && !model.isEmpty() && isValidYear(year, 2020);
    }
    private boolean isValidYear(int current_year, int min_year){
        return current_year>=min_year && current_year<= LocalDate.now().getYear();
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
        if (existingInventoryOptional.isPresent()) {
            Inventory existingInventory = existingInventoryOptional.get();
            ResponseEntity<String> BAD_REQUEST = validationCheck(updateInventory, existingInventory);
            if (BAD_REQUEST != null) return BAD_REQUEST;
            return updateAndModifiedStatus(updateInventory, existingInventory);
        } else {
            return ResponseEntity.notFound().build();
        }
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
        } else existingInventory.setStatus("MODIFIED");
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
}

