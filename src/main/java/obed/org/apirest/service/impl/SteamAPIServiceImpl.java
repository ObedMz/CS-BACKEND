package obed.org.apirest.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import obed.org.apirest.entities.InventoryItem;
import obed.org.apirest.service.SteamAPIService;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SteamAPIServiceImpl implements SteamAPIService {
    @Override
    public List<InventoryItem> fetchData() {
        ClassPathResource resource = new ClassPathResource("data.json");
        ObjectMapper mapper = new ObjectMapper();
        try {
            return List.of(mapper.readValue(resource.getInputStream(), InventoryItem[].class));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
