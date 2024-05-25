package obed.org.apirest.service;

import lombok.Getter;
import lombok.Setter;
import obed.org.apirest.model.data.GroupsDTO;
import obed.org.apirest.model.data.ItemDTO;
import obed.org.apirest.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class ItemService {
    private final ExecutorService executorService = Executors.newFixedThreadPool(20);

    private final Map<String, GroupsDTO> groupsMap = new LinkedHashMap<>();
    public ItemService(){
        groupsMap.put("Primary", new GroupsDTO("Primary", new HashSet<>()));
        groupsMap.put("Secondary", new GroupsDTO("Secondary", new HashSet<>()));
        groupsMap.put("Knifes", new GroupsDTO("Knifes", new HashSet<>()));
        groupsMap.put("Gloves", new GroupsDTO("Gloves", new HashSet<>()));
        groupsMap.put("Others", new GroupsDTO("Others", new HashSet<>()));
    }

    @Getter
    @Setter
    private Float globalPercentage = 0f;
    private ConcurrentHashMap<String, Set<String>> groupedItems = new ConcurrentHashMap<>();

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private MongoTemplate mongoTemplate;

    public ItemDTO updateItem(ItemDTO item) {
        return itemRepository.save(item);
    }
    public List<ItemDTO> getAll() {
        return itemRepository.findAll();
    }
    public Page<ItemDTO> getAll(Pageable pageable) {
        return itemRepository.findAll(pageable);
    }

    public ItemDTO getById(String id) {
        return itemRepository.findById(id).orElse(null);
    }

    public ItemDTO getByName(String name) {
        return itemRepository.findByName(name);
    }

    public void save(List<ItemDTO> items) {
        items.forEach(item -> executorService.submit(() -> {
            try {
                ItemDTO existingItem = itemRepository.findById(item.getId()).orElse(null);
                if (existingItem == null) {
                    itemRepository.save(item);
                    return;
                }
                item.setAddedPercentage(existingItem.getAddedPercentage());
                item.setHidden(existingItem.getHidden());
                if (existingItem.getModified() != null) {
                    item.setModified(existingItem.getModified());
                    item.setCustom_price(existingItem.getCustom_price());
                }
                itemRepository.save(item);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));
        shutdownExecutorService();
    }
    private void shutdownExecutorService() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
                if (!executorService.awaitTermination(60, TimeUnit.SECONDS))
                    System.err.println("ExecutorService no se ha cerrado.");
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private Pageable createPageable(Map<String, String> filters) {
        Integer page = parseIntOrDefault(filters.get("page"), 0);
        Integer size = parseIntOrDefault(filters.get("size"), 54);
        String sort = filters.getOrDefault("sort", "price");
        String order = filters.getOrDefault("order", "asc");

        return PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort));
    }
    private Integer parseIntOrDefault(String value, Integer defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private Query createQuery(Map<String, String> filters) {
        Arrays.asList("page", "size", "sort", "order").forEach(filters.keySet()::remove);

        Query query = new Query();
        filters.forEach((key, value) -> query.addCriteria(Criteria.where(key).is(value)));

        return query;
    }
    public List<ItemDTO> filterItems(Map<String, String> filters) {
        Pageable pageable = createPageable(filters);
        Query query = createQuery(filters);
        query.with(pageable);
        return mongoTemplate.find(query, ItemDTO.class);
    }

    public List<ItemDTO> searchItems(String searchTerm) {
        Query query = new Query();
        query.addCriteria(Criteria.where("displayName").regex(searchTerm, "i"));
        query.limit(100);

        return mongoTemplate.find(query, ItemDTO.class);

    }

    public void updateGroupsAsync() {
        Thread thread = new Thread(() -> {
            List<ItemDTO> items = getAll();

            Map<String, Set<String>> newGroupedItems = items.stream()
                    .collect(Collectors.groupingBy(ItemDTO::getGroup,
                            Collectors.mapping(ItemDTO::getItemType, Collectors.toSet())));

            groupedItems = new ConcurrentHashMap<>(newGroupedItems);
            System.out.println("Groups updated successfully -> " + groupedItems.size());
        });
        thread.start();
    }
    public List<ItemDTO> getItemsByCategory(String category) {
        return switch (category.toLowerCase()) {
            case "primary" -> itemRepository.findByGroupIn(Arrays.asList("rifle", "sniper rifle", "machinegun", "smg"));
            case "secondary" -> itemRepository.findByGroup("pistol");
            case "knife" -> itemRepository.findByGroup("knife");
            case "gloves" -> itemRepository.findByGroup("gloves");
            case "agent" -> itemRepository.findByGroup("agent");
            case "others" -> itemRepository.findByGroupIn(Arrays.asList("sticker", "graffiti", "music kit"));
            default -> throw new IllegalArgumentException("Invalid category: " + category);
        };
    }
    public Set<GroupsDTO> getTypes() {
        groupedItems.forEach((key, value) -> {
            String lowerKey = key.toLowerCase();
            GroupsDTO group = groupsMap.getOrDefault(getGroup(lowerKey), groupsMap.get("Others"));
            group.getItemTypes().addAll(
                    value.stream()
                            .filter(Objects::nonNull)
                            .collect(Collectors.toSet())
            );
        });
        return new HashSet<>(groupsMap.values());
    }

    private String getGroup(String key) {
        return switch (key) {
            case "rifles", "shotguns", "smg", "machinegun", "sniper rifle" -> "Primary";
            case "pistol" -> "Secondary";
            case "gloves" -> "Gloves";
            case "knife" -> "Knifes";
            default -> "Others";
        };
    }


}
