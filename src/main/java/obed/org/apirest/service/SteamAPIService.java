package obed.org.apirest.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import obed.org.apirest.model.ItemDTO;
import obed.org.apirest.model.RawItemData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SteamAPIService {
    private static final long COOLDOWN_TIME_MS = 600000;
    private long lastAccessTime = 0;


    @Autowired
    private ItemService itemService;
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
    public Boolean isCoolDown() {
        long currentTime = System.currentTimeMillis();
        return (currentTime - lastAccessTime) < COOLDOWN_TIME_MS;
    }

    public void updateCoolDown() {
        lastAccessTime = System.currentTimeMillis();
    }
}
