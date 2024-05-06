package obed.org.apirest.controllers;

import obed.org.apirest.model.ItemData;
import obed.org.apirest.service.ItemService;
import obed.org.apirest.service.SteamAPIService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
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
    public ResponseEntity<List<ItemData>> filterItems(@RequestParam Map<String, String> filters) {
        return ResponseEntity.ok(itemService.filterItems(filters));
    }



    @PatchMapping("/update")
    public ResponseEntity<Void> updateItems() {
        steamAPIService.fetchData();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/items/{id}")
    public ResponseEntity<ItemData> getItemById(@PathVariable String id) {
        ItemData item = itemService.getById(id);
        return ResponseEntity.ok(item);
    }

    @GetMapping("/items/name/{name}")
    public ResponseEntity<ItemData> getItemsByName(@PathVariable String name) {
        ItemData items = itemService.getByName(name);
        return ResponseEntity.ok(items);
    }

}
