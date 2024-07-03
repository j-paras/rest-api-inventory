package com.bootcamp.inventory.service;

import com.bootcamp.inventory.entity.Inventory;
import com.bootcamp.inventory.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    public Optional<Inventory> updateInventory(Long id, Inventory updateInventory){
        try{
            Optional<Inventory> existingInventoryOptional=inventoryRepository.findById(id);
            if(existingInventoryOptional.isPresent()){
                Inventory existingInventory = existingInventoryOptional.get();
                existingInventory.setCost(updateInventory.getCost());
                existingInventory.setSellingPrice(updateInventory.getSellingPrice());
                existingInventory.setLocation(updateInventory.getLocation());
                existingInventory.setUpdatedAt(LocalDateTime.now());
                Inventory saveInventory=inventoryRepository.save(existingInventory);
                return Optional.of(saveInventory);
            }else {
                return Optional.empty();
            }
        }catch (Exception e){
            throw new RuntimeException("Failed to update the inventory:" + e.getMessage());
        }
    }

}
