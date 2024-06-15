package obed.org.apirest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class InventoryScheduler {
    @Autowired
    private RawItemService rawItemService;

    @Autowired
    private SteamAPIService steamAPIService;
    @Autowired
    private ItemService itemService;

    @Scheduled (fixedRate = 3600000)
    public void updateInventory() {

        System.out.println("Updating items asynchronously...");
        steamAPIService.fetchData();

        rawItemService.fetchData(steamAPIService.getSteamData().getSteamIDs());
        System.out.println("updating groups asynchronously...");
        itemService.updateGroupsAsync();

    }
}
