package obed.org.apirest.controllers;

import jakarta.websocket.server.PathParam;
import obed.org.apirest.model.ItemDTO;
import obed.org.apirest.service.ItemService;
import obed.org.apirest.service.SteamAPIService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1")
public class ItemController {
    @Autowired
    private ItemService itemService;

    @Autowired
    private SteamAPIService steamAPIService;

    @GetMapping("/items")
    public ResponseEntity<List<ItemDTO>> filterItems(@RequestParam Map<String, String> filters) {
        return ResponseEntity.ok(itemService.filterItems(filters));
    }

    @PatchMapping("/update")
    public ResponseEntity<Void> updateItems() {
        if(steamAPIService.isCoolDown()) return ResponseEntity.status(429).build();
        steamAPIService.fetchData();
        steamAPIService.updateCoolDown();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/items/{id}")
    public ResponseEntity<ItemDTO> getItemById(@PathVariable String id) {
        ItemDTO item = itemService.getById(id);
        return ResponseEntity.ok(item);
    }

    @GetMapping("/items/name/{name}")
    public ResponseEntity<ItemDTO> getItemsByName(@PathVariable String name) {
        ItemDTO items = itemService.getByName(name);
        return ResponseEntity.ok(items);
    }

    @PatchMapping("/items/{id}")
    public ResponseEntity<ItemDTO> updateItem(@PathVariable String id,
                                              @RequestParam(value = "addedPercentage", defaultValue = "0.0") Float addedPercentage,
                                              @RequestParam(value = "hidden", defaultValue = "false") Boolean hidden) {
        ItemDTO item = itemService.getById(id);
        if (item == null) return ResponseEntity.notFound().build();
        item.setAddedPercentage(addedPercentage);
        item.setHidden(hidden);
        ItemDTO updatedItem = itemService.updateItem(item);
        return ResponseEntity.ok(updatedItem);
    }



}
