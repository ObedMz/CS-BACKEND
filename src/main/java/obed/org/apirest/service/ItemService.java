package obed.org.apirest.service;

import obed.org.apirest.model.ItemDTO;
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

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class ItemService {

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
        items.forEach(item -> {
            try {
                ItemDTO existingItem = itemRepository.findById(item.getId()).orElse(null);
                if (existingItem == null) {
                    itemRepository.save(item);
                    return;
                }
                item.setAddedPercentage(existingItem.getAddedPercentage());
                item.setHidden(existingItem.getHidden());
                itemRepository.save(item);
            }catch (Exception e) {
               e.printStackTrace();
            }

        });
    }

    private Pageable createPageable(Map<String, String> filters) {
        Integer page = parseIntOrDefault(filters.get("page"), 0);
        Integer size = parseIntOrDefault(filters.get("size"), 30);
        String sort = filters.getOrDefault("sort", "id");
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
}
