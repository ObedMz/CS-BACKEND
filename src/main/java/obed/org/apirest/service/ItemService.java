package obed.org.apirest.service;

import obed.org.apirest.model.ItemData;
import obed.org.apirest.repository.ItemRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class ItemService {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private MongoTemplate mongoTemplate;

    public List<ItemData> getAll() {
        return itemRepository.findAll();
    }
    public Page<ItemData> getAll(Pageable pageable) {
        return itemRepository.findAll(pageable);
    }

    public ItemData getById(String id) {
        return itemRepository.findById(id).orElse(null);
    }

    public ItemData getByName(String name) {
        return itemRepository.findByitemname(name);
    }

    public void save(List<ItemData> items) {
        items.forEach(item -> {
            ItemData existingItem = itemRepository.findById(item.getId()).orElse(null);
            if (existingItem == null) {
                itemRepository.save(item);
                return;
            }
            updateItemData(existingItem, item);
            itemRepository.save(existingItem);

        });
    }
    private void updateItemData(ItemData itemExist, ItemData updateItem) {
        BeanUtils.copyProperties(updateItem, itemExist, "hidden", "addedPercentage");
        if (updateItem.getHidden() != null) {
            itemExist.setHidden(updateItem.getHidden());
        }
        if (updateItem.getAddedPercentage() != null) {
            itemExist.setAddedPercentage(updateItem.getAddedPercentage());
        }
    }
    private Query buildQueryFromFilter(ItemData filterDTO) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        Field[] fields = filterDTO.getClass().getDeclaredFields();

        for (Field field : fields) {
            ReflectionUtils.makeAccessible(field);
            Object value = ReflectionUtils.getField(field, filterDTO);
            if (value != null && !Objects.equals(value, "")) {
                criteria = criteria.and(field.getName()).regex(value.toString(), "i");
            }
        }

        query.addCriteria(criteria);
        return query;
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
    public List<ItemData> filterItems(Map<String, String> filters) {
        Pageable pageable = createPageable(filters);
        Query query = createQuery(filters);
        query.with(pageable);
        return mongoTemplate.find(query, ItemData.class);
    }
}
