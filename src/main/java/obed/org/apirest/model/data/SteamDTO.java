package obed.org.apirest.model.data;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class SteamDTO {
    private String key;

    private Set<String> steamIDs;

}
