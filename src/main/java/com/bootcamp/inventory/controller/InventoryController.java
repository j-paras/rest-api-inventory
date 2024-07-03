package com.bootcamp.inventory.controller;

import com.bootcamp.inventory.entity.Inventory;
import com.bootcamp.inventory.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/inventory")
public class InventoryController {
    @Autowired
    private InventoryService inventoryService;

    @PutMapping(path = "/{id}")
    public ResponseEntity<?> updateInventory(@PathVariable("id") Long id, @RequestBody Inventory inventory) {
        try {
            ResponseEntity<?> responseEntity = inventoryService.updateInventory(id, inventory);
            return responseEntity;
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update inventory: " + e.getMessage());
        }
    }
}
