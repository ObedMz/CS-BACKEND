package obed.org.apirest.service.impl;

import obed.org.apirest.entities.InventoryItem;
import obed.org.apirest.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class InventoryService {

    @Autowired
    private CacheService cacheService;

    public List<InventoryItem> getAll() {
        return cacheService.getStoredData();
    }

    public InventoryItem getById(String id) {
        return getAll().stream().filter(item -> item.getId().equals(id)).findFirst().orElse(null);
    }

    public InventoryItem getByName(String name) {
        return getAll().stream().filter(item -> item.getItemname().equals(name)).findFirst().orElse(null);
    }

    public HashSet<InventoryItem> filterItems(Map<String, String> filters) {
        List<InventoryItem> items = getAll();

        for (Map.Entry<String, String> entry : filters.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            Predicate<InventoryItem> filterPredicate = getFilterPredicate(key, value);
            items = items.stream().filter(filterPredicate).collect(Collectors.toList());
        }

        return new HashSet<>(items);
    }

    private Predicate<InventoryItem> getFilterPredicate(String key, String value) {
        return switch (key) {
            case "id" -> item -> item.getId().equals(value);
            case "rarity" -> item -> item.getRarity().equals(value);
            case "quality" -> item -> item.getQuality().equals(value);
            case "tradeable" -> InventoryItem::isTradable;
            case "type" -> item -> item.getTags().stream().anyMatch(tag -> tag.getCategory().equals(key) && tag.getLocalized_tag_name().equals(value));
            default -> throw new IllegalArgumentException("Invalid filter key: " + key);
        };
    }
}
