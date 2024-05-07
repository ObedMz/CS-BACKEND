package obed.org.apirest.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.HashSet;
import java.util.Set;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RawItemData {

    @Id
    private String id;
    private String marketname;
    private String itemgroup;
    private String itemname;
    private String itemtype;
    private double pricereal;
    private String bordercolor;
    private String color;
    private String image;
    private boolean tradable;
    private String rarity;
    private String quality;
    private Set<Description> descriptions = new HashSet<>();
    private String inspectlink;
    private String wear;
    private Integer markettradablerestriction;

    @Data
    public static class Description {
        private String type;
        private String value;
        private String color;
    }
}
