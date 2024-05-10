package obed.org.apirest.model.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.List;

@Data
@AllArgsConstructor
public class SteamDTO {
    @Id
    private String id;

    private String key;

    private List<String> steamIDs;

}
