package obed.org.apirest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executors;

@Service
public class InventoryScheduler {

    @Autowired
    private SteamAPIService steamAPIService;
    @Autowired
    private ItemService itemService;

    @Scheduled (fixedRate = 3600000)
    public void updateInventory() {
        Executors.newSingleThreadScheduledExecutor().execute(() -> {
            System.out.println("Updating items asynchronously...");
            steamAPIService.fetchData();
            System.out.println("updating groups asynchronously...");
            itemService.updateGroupsAsync();
        });

    }
}
