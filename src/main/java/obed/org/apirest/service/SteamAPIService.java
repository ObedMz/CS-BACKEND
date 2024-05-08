package obed.org.apirest.service;

import obed.org.apirest.model.data.ItemDTO;
import obed.org.apirest.model.data.RawItemData;
import obed.org.apirest.model.data.SteamDTO;
import obed.org.apirest.repository.SteamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class SteamAPIService {
    @Autowired
    private SteamRepository steamRepository;
    @Autowired
    private ItemService itemService;
    @Autowired
    private RestTemplate restTemplate;
    private static final Long COOLDOWN_TIME_MS = 1000L;
    private long lastAccessTime = 0;
    @Value("${steam.api.url}")
    private String URL;

    public SteamDTO getSteamData() {
        Optional<SteamDTO> firstItem = steamRepository.findAll().stream().findFirst();
        return firstItem.orElse(null);
    }
    public SteamDTO saveSteamData(SteamDTO steamDTO) {
        steamRepository.saveAll(List.of(steamDTO));
        updateDataAsync();
        return getSteamData();
    }


     public void fetchData() {
        SteamDTO steamDTO = getSteamData();
        if (steamDTO == null) return;
        updateCoolDown();
        for (String steamId : steamDTO.getSteamIDs()) {
            String apiUrl = URL +  steamDTO.getKey() + "&steam_id=" + steamId;
            RawItemData[] rawItems = restTemplate.getForObject(apiUrl, RawItemData[].class);
            if (rawItems == null) continue;
            List<ItemDTO> items = Arrays.stream(rawItems)
                    .map(ItemDTO::createByRawItem)
                    .collect(Collectors.toList());
            itemService.save(items);
        }
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
