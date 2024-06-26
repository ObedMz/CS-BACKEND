package obed.org.apirest.controllers;

import obed.org.apirest.model.data.GroupsDTO;
import obed.org.apirest.model.data.ItemDTO;
import obed.org.apirest.service.ItemService;
import obed.org.apirest.service.SteamAPIService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

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

    @GetMapping("items/search")
    public ResponseEntity<List<ItemDTO>> searchItems(@RequestParam String searchTerm) {
        return ResponseEntity.ok(itemService.searchItems(searchTerm));
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

    @PatchMapping("/admin/items/{id}")
    public ResponseEntity<ItemDTO> updateItem(@PathVariable String id,
                                              @RequestParam(value = "addedPercentage", defaultValue = "0.0") Float addedPercentage,
                                              @RequestParam(value = "hidden", defaultValue = "false") Boolean hidden,
                                              @RequestParam(value = "price", required = false) Double price) {
        ItemDTO item = itemService.getById(id);
        if (item == null) return ResponseEntity.notFound().build();
        item.setAddedPercentage(addedPercentage);
        item.setHidden(hidden);
        if(price != null) {
            item.setModified(true);
            item.setCustom_price(price);
        } else {
            item.setModified(false);
            item.setCustom_price(0.0);
        }
        ItemDTO updatedItem = itemService.updateItem(item);
        return ResponseEntity.ok(updatedItem);
    }

    @PatchMapping("/admin/update")
    public ResponseEntity<Void> updateItems() {
        if(steamAPIService.isCoolDown()) return ResponseEntity.status(429).build();
        CompletableFuture.runAsync(()-> {
            System.out.println("Updating items asynchronously...");
            steamAPIService.fetchData();
            System.out.println("updating groups asynchronously...");
            itemService.updateGroupsAsync();
        });
        return ResponseEntity.ok().build();
    }

    @GetMapping("/items/global")
    public ResponseEntity<Float> getGlobalPercentage() {
        return ResponseEntity.ok(itemService.getGlobalPercentage());
    }
    @PostMapping("/admin/global")
    public ResponseEntity<Integer> setGlobalPercentage(@RequestParam Float globalPercentage) {
        itemService.setGlobalPercentage(globalPercentage);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/items/types")
    public ResponseEntity<Set<GroupsDTO>> getTypes() {
        return ResponseEntity.ok(itemService.getTypes());
    }

    @GetMapping("items/filter")
    public ResponseEntity<List<ItemDTO>> getFilters(@RequestParam String group) {
        return ResponseEntity.ok(itemService.getItemsByCategory(group));
    }

}
