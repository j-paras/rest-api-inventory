package com.bootcamp.inventory.controller;

import com.bootcamp.inventory.entity.Inventory;
import com.bootcamp.inventory.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/inventory")
public class InventoryController {
    @Autowired
    private InventoryService inventoryService;

    @PutMapping(path = "/{id}")
    public ResponseEntity<Inventory> updateInventory(@PathVariable("id") Long id, @RequestBody Inventory inventory){
        Optional<Inventory> inventoryOptional=inventoryService.updateInventory(id, inventory);
        return inventoryOptional.isPresent()?ResponseEntity.ok(inventoryOptional.get()):ResponseEntity.notFound().build();
    }
}
