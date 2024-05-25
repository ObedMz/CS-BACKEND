package obed.org.apirest.service;

import obed.org.apirest.model.data.ItemDTO;
import obed.org.apirest.model.data.RawItemData;
import obed.org.apirest.model.data.SteamDTO;
import obed.org.apirest.repository.ItemRepository;
import obed.org.apirest.repository.SteamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class SteamAPIService {
    @Autowired
    private SteamRepository steamRepository;

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private ItemService itemService;
    @Autowired
    private RestTemplate restTemplate;
    private static final Long COOLDOWN_TIME_MS = 1000L;
    private long lastAccessTime = 0;
    @Value("${steam.api.url}")
    private String URL;

    public SteamDTO getSteamData() {
        Optional<SteamDTO> firstItem = steamRepository.findById("default");
        return firstItem.orElse(null);
    }
    public SteamDTO saveSteamData(SteamDTO steamDTO) {
        steamDTO.setId("default");
        SteamDTO saved = steamRepository.save(steamDTO);
        updateDataAsync();
        return saved;
    }


    public void fetchData() {
        SteamDTO steamDTO = getSteamData();
        if (steamDTO == null) return;
        updateCoolDown();
        steamDTO.getSteamIDs()
                .forEach(steamId -> CompletableFuture.runAsync(() -> {
                    String apiUrl = URL + steamDTO.getKey() + "&steam_id=" + steamId + "&no_cache=true";
                    System.out.println("apiUrl: " + apiUrl);
                    RawItemData[] rawItems = restTemplate.getForObject(apiUrl, RawItemData[].class);
                    if (rawItems == null) return;

                    List<ItemDTO> items = Arrays.stream(rawItems)
                            .map(ItemDTO::createByRawItem)
                            .collect(Collectors.toList());
                    itemService.save(items);

                    Set<String> externalDataIds = items.stream()
                            .map(ItemDTO::getId)
                            .collect(Collectors.toSet());
                    List<String> currentDataIds = itemRepository.findAllIds();
                    deleteMissingData(currentDataIds, externalDataIds);
                }));
    }
    private void deleteMissingData(List<String> currentDataIds, Set<String> externalDataIds) {
        Set<String> idsToDelete = currentDataIds.stream()
                .filter(id -> !externalDataIds.contains(id))
                .collect(Collectors.toSet());
        System.out.println("idsToDelete: " + idsToDelete);
        itemRepository.deleteAllByIdIn(idsToDelete);
    }


    @Async
    public void updateDataAsync() {
        CompletableFuture.runAsync(this::fetchData);
    }


    public Boolean isCoolDown() {
        long currentTime = System.currentTimeMillis();
        return (currentTime - lastAccessTime) < COOLDOWN_TIME_MS;
    }

    public void updateCoolDown() {
        lastAccessTime = System.currentTimeMillis();
    }
}
