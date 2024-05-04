package obed.org.apirest.service;

import obed.org.apirest.entities.InventoryItem;

import java.util.List;

public interface CacheService {

    List<InventoryItem> getStoredData();
    void updateCache();
}
