package obed.org.apirest.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import obed.org.apirest.model.data.ItemDTO;
import obed.org.apirest.model.data.RawItemData;
import obed.org.apirest.model.data.SteamDTO;
import obed.org.apirest.repository.SteamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
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
    private ItemService itemService;
    @Autowired
    private RestTemplate restTemplate;
    private static final Long COOLDOWN_TIME_MS = 600000L;
    private long lastAccessTime = 0;
    @Value("${steam.api.url}")
    private String URL;
    @Value("${steam.api.key}")
    private String KEY;
    @Value("${steam.userKeys}")
    private String[] userKeys;


    private SteamDTO initializeSteamDTO() {
        SteamDTO steamDTO = getSteamData();
        if (steamDTO == null) {
            steamDTO = new SteamDTO(KEY, Set.of(userKeys));
            saveSteamData(steamDTO);
        }
        return steamDTO;
    }

    public SteamDTO getSteamData() {
        Optional<SteamDTO> firstItem = steamRepository.findAll().stream().findFirst();
        return firstItem.orElse(null);
    }
    public SteamDTO saveSteamData(SteamDTO steamDTO) {
        steamRepository.saveAll(List.of(steamDTO));
        return getSteamData();
    }


    /**
     public void fetchData() {
        SteamDTO steamDTO = getSteamData();
        if (steamDTO == null) return;
        updateCoolDown();
        for (String userKey : steamDTO.getUserKeys()) {
            String apiUrl = URL + "?userKey=" + userKey;
            RawItemData[] rawItems = restTemplate.getForObject(apiUrl, RawItemData[].class);
            if (rawItems == null) continue;
            List<ItemDTO> items = Arrays.stream(rawItems)
                    .map(ItemDTO::createByRawItem)
                    .collect(Collectors.toList());
            itemService.save(items);
        }
    }
     */

    public void fetchData() {
        try {
            ClassPathResource resource = new ClassPathResource("data.json");
            ObjectMapper mapper = new ObjectMapper();
            RawItemData[] rawItems = mapper.readValue(resource.getInputStream(), RawItemData[].class);
            List<ItemDTO> items = Arrays.stream(rawItems)
                    .map(ItemDTO::createByRawItem)
                    .collect(Collectors.toList());
            itemService.save(items);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Async
    public void updateDataAsync() {
        CompletableFuture.runAsync(() -> {
            updateCoolDown();
            fetchData();
        });
    }


    public Boolean isCoolDown() {
        long currentTime = System.currentTimeMillis();
        return (currentTime - lastAccessTime) < COOLDOWN_TIME_MS;
    }

    public void updateCoolDown() {
        lastAccessTime = System.currentTimeMillis();
    }
}
