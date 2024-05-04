package obed.org.apirest.controllers;

import obed.org.apirest.entities.InventoryItem;
import obed.org.apirest.service.impl.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Map;

@RestController
@RequestMapping("/v1")
public class InventoryController {
    @Autowired
    private InventoryService inventoryService;

    @GetMapping("/items")
    public ResponseEntity<HashSet<InventoryItem>> filterItems(@RequestParam Map<String, String> filters) {
        HashSet<InventoryItem> filteredItems = inventoryService.filterItems(filters);
        return ResponseEntity.ok(filteredItems);
    }

    @GetMapping("/items/{id}")
    public ResponseEntity<InventoryItem> getItemById(@PathVariable String id) {
        InventoryItem item = inventoryService.getById(id);
        return ResponseEntity.ok(item);
    }

    @GetMapping("/items/name/{name}")
    public ResponseEntity<InventoryItem> getItemsByName(@PathVariable String name) {
        InventoryItem items = inventoryService.getByName(name);
        return ResponseEntity.ok(items);
    }

}
