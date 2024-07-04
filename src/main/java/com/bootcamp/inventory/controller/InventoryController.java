package com.bootcamp.inventory.controller;

import com.bootcamp.inventory.entity.Inventory;
import com.bootcamp.inventory.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/inventory")
public class InventoryController {
    @Autowired
    private InventoryService inventoryService;

    @GetMapping("/page/{pageNumber}/{pageSize}")
    public ResponseEntity<List<Inventory>> getAllInventory(@RequestParam(defaultValue = "0") int pageNumber,
                                                           @RequestParam(defaultValue = "10") int pageSize){
        List<Inventory> inventories=inventoryService.getAllInventory(pageNumber,pageSize);
        return ResponseEntity.ok(inventories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<Inventory>> getInventoryById(@PathVariable Long id) {
        Optional<Inventory> inventory = inventoryService.getInventoryById(id);
        return ResponseEntity.ok(inventory);
    }

    @PostMapping("/create")
    public ResponseEntity<Inventory> saveInventory(@RequestBody Inventory inventory){
        Inventory savedInventory=inventoryService.saveInventory(inventory);
        return ResponseEntity.ok(savedInventory);
    }

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
