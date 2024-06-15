package obed.org.apirest.model.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RawSteamDto {
    @Id
    private long classid;
    private Integer daysremaining;
}
