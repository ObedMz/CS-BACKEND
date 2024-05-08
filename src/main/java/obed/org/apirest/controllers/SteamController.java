package obed.org.apirest.controllers;

import obed.org.apirest.model.data.SteamDTO;
import obed.org.apirest.service.SteamAPIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/v1/steam")
public class SteamController {
    @Autowired
    private SteamAPIService steamAPIService;

    @GetMapping("/info")
    public ResponseEntity<SteamDTO> getInfo() {
        SteamDTO steamDTO = steamAPIService.getSteamData();
        if(steamDTO == null) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(steamDTO);
    }
    @PatchMapping("/update")
    public ResponseEntity<SteamDTO> updateInfo(@RequestBody SteamDTO steamDTO) {
        if(steamDTO == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(steamAPIService.saveSteamData(steamDTO));
    }
}
