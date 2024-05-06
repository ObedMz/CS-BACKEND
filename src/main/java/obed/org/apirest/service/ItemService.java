package obed.org.apirest.service;

import obed.org.apirest.model.ItemData;
import obed.org.apirest.repository.ItemRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Service
public class ItemService {

    @Autowired
    private ItemRepository itemRepository;

    public List<ItemData> getAll() {
        return itemRepository.findAll();
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


    public List<ItemData> filterItems(Map<String, String> filters) {
        Query query = new Query();
        filters.forEach((key, value) -> {
            query.addCriteria(Criteria.where(key).is(value));
        });
        return  itemRepository.findByCustomQuery(query);
    }
}
