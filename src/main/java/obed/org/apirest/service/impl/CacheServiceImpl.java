package obed.org.apirest.service.impl;

import obed.org.apirest.entities.InventoryItem;
import obed.org.apirest.service.CacheService;
import obed.org.apirest.service.SteamAPIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class CacheServiceImpl implements CacheService {
    private static final long CACHE_EXPIRATION_TIME = 3600000;
    private volatile List<InventoryItem> cachedItems;
    private long lastUpdateTime;
    @Autowired
    private SteamAPIService SteamAPIServiceImpl;

    @Override
    public List<InventoryItem> getStoredData() {
        if (cachedItems == null || System.currentTimeMillis() - lastUpdateTime >= CACHE_EXPIRATION_TIME) {
            synchronized (this) {
                if (cachedItems == null || System.currentTimeMillis() - lastUpdateTime >= CACHE_EXPIRATION_TIME) {
                    updateCache();
                }
            }
        }
        return cachedItems;
    }

    @Override
    public void updateCache() {
        cachedItems = new CopyOnWriteArrayList<>(SteamAPIServiceImpl.fetchData());
        lastUpdateTime = System.currentTimeMillis();
        System.out.println("La caché ha sido actualizada.");
    }
}
