package com.bootcamp.inventory.controller;

import com.bootcamp.inventory.entity.Inventory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

public class InventoryController {
    @PutMapping(path = "/{id}")
    public ResponseEntity<Inventory> updateInventory(@PathVariable("id") Long id, @RequestBody Inventory inventory){
        Optional<Inventory> inventoryOptional=inventoryService.updateInventory(id, inventory);
        return inventoryOptional.isPresent()?ResponseEntity.ok(inventoryOptional.get()):ResponseEntity.notFound().build();
    }
}
