package obed.org.apirest.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import obed.org.apirest.model.data.ItemDTO;
import obed.org.apirest.model.data.RawItemData;
import obed.org.apirest.model.data.RawSteamDto;
import obed.org.apirest.repository.RawItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RawItemService {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private SteamAPIService steamAPIService;

    @Autowired
    private RawItemRepository rawItemRepository;

    public void fetchData(){
        for(String steamID : steamAPIService.getSteamData().getSteamIDs()){
            String apiUrl = "https://steamcommunity.com/inventory/" + steamID + "/730/2?l=english";
            ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);
            String responseBody = response.getBody();
            getJsonFromSteam(responseBody).forEach(node -> {
                RawSteamDto rawSteamDto = processJson(node);
                if(rawSteamDto == null) return;

                System.out.println("RawSteamDto: " + rawSteamDto.getClassid());
                rawItemRepository.save(rawSteamDto);
            });
        }

    }
    public JsonNode getJsonFromSteam(String responseBody) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode descriptionsNode = rootNode.get("descriptions");
            return descriptionsNode;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public RawSteamDto processJson(JsonNode rootNode) {
        if (rootNode.has("owner_descriptions")) {
            ArrayNode ownerDescriptionsNode = (ArrayNode) rootNode.get("owner_descriptions");
            Iterator<JsonNode> elements = ownerDescriptionsNode.elements();

            boolean containsTradable = false;
            String tradableDateValue = null;

            while (elements.hasNext()) {
                JsonNode descriptionNode = elements.next();
                String value = descriptionNode.get("value").asText();
                if (value.contains("Tradable")) {
                    containsTradable = true;
                    tradableDateValue = value;
                }
            }

            if (containsTradable) {
                Long classid = rootNode.get("classid").asLong();
                RawSteamDto rawSteamDto = new RawSteamDto();
                rawSteamDto.setClassid(classid);
                System.out.println("id: " + rawSteamDto.getClassid());
                Integer days = calculateDaysRemaining(tradableDateValue);
                rawSteamDto.setDaysremaining(days);
                return rawSteamDto;
            }
        }
        return null;
    }


    public List<RawSteamDto> findAll(){
        return rawItemRepository.findAll();
    }

    public RawSteamDto findById(Long id){
        return rawItemRepository.findById(id).get();
    }

    private Integer calculateDaysRemaining(String tradableDateValue) {
        try {
            String dateString = tradableDateValue.replace("Tradable After ", "").replaceAll(" GMT", "");

            SimpleDateFormat formatoFecha = new SimpleDateFormat("MMM dd, yyyy (HH:mm:ss)", Locale.ENGLISH);
            Date fecha = formatoFecha.parse(dateString);
            Date fechaActual = new Date();
            long diferenciaMilisegundos = fecha.getTime() - fechaActual.getTime();
            Long diasRestantes = diferenciaMilisegundos / (1000 * 60 * 60 * 24);
            return diasRestantes.intValue();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }


    }
}
