package obed.org.apirest.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import obed.org.apirest.model.ItemData;

import obed.org.apirest.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SteamAPIService {

    @Autowired
    private ItemService itemService;
    public void fetchData() {
        ClassPathResource resource = new ClassPathResource("data.json");
        ObjectMapper mapper = new ObjectMapper();
        try {
            List<ItemData> items = List.of(mapper.readValue(resource.getInputStream(), ItemData[].class));
            itemService.save(items);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
