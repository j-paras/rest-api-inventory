package com.bootcamp.inventory.controller;

import com.bootcamp.inventory.controller.dto.CreateInventoryRequest;
import com.bootcamp.inventory.controller.dto.CreateInventoryResponse;
import com.bootcamp.inventory.controller.dto.PatchInventoryRequest;
import com.bootcamp.inventory.entity.Inventory;
import com.bootcamp.inventory.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

// REST -> Resouce - Plural
// /api/v1/inventory/1
// /api/v1/inventories/{id}
@RestController
@RequestMapping("/api/v1/inventories")
public class InventoryController {
    @Autowired
    private InventoryService inventoryService;

    // GET api/v1/inventories/page/1/pagesize/10
    // GET api/v1/inventories?page=1&pageSize=10
    // GET api/v1/ads?category=car&make=honda
    // TODO - Update path to have query params for page and size
    @GetMapping("/page/{pageNumber}/{pageSize}")
    public ResponseEntity<List<Inventory>> getAll(@RequestParam(defaultValue = "0") int pageNumber,
                                                  @RequestParam(defaultValue = "10") int pageSize){
        List<Inventory> inventories = inventoryService.getAll(pageNumber,pageSize);
        return ResponseEntity.ok(inventories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<Inventory>> getById(@PathVariable @Validated Long id) {
        Optional<Inventory> inventory = inventoryService.getById(id);
        return ResponseEntity.ok(inventory);
    }

    // REST -> Do need verbs
    // POST /api/v1/inventories/create
    // DELETE /api/v1/inventories/{ID}
    // POST /api/v1/inventories/
    @PostMapping("/")
    public ResponseEntity<CreateInventoryResponse> save(@RequestBody CreateInventoryRequest createInventoryRequest){
        Inventory savedInventory = inventoryService.save(createInventoryRequest);
        return ResponseEntity.ok(mapInventoryToCreateInventoryRespone(savedInventory));
    }

    private CreateInventoryResponse mapInventoryToCreateInventoryRespone(Inventory savedInventory) {
        return new CreateInventoryResponse(savedInventory.getSku());
    }

    // PUT /api/v1/inventories/1
    // PUT VS POST VS PATCH
    // PUT - Update ->
    // PUT - Create / Update ->
    // PATCH - Update - 404
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

    @PatchMapping(path = "/{id}")
    public ResponseEntity<?> update(@PathVariable("id") Long id, @RequestBody PatchInventoryRequest patchInventoryRequest) {
        inventoryService.update(id, patchInventoryRequest);
        return (ResponseEntity<?>) ResponseEntity.status(204);
    }
}
