package obed.org.apirest.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.Getter;
import org.springframework.data.annotation.Id;

import java.util.HashSet;
import java.util.Set;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class InventoryItem {
    @Id
    private String id;
    private String markethashname;
    private String marketname;
    private String slug;
    private int count;
    private String assetid;
    private String classid;
    private String groupid;
    private String instanceid;
    private int nameid;
    private String hashid;
    private String itemgroup;
    private String itemname;
    private String itemtype;
    private PriceUpdate priceupdatedat;
    private double pricereal;
    private String bordercolor;
    private String color;
    private String image;
    private boolean tradable;
    private String rarity;
    private String quality;

    private Set<Tag> tags = new HashSet<>();
    private Set<Description> descriptions = new HashSet<>();
    private Set<Action> actions = new HashSet<>();
    private String inspectlink;
    private InspectLinkParsed inspectlinkparsed;

    @Data
    public static class Action {
        private String link;
        private String name;
    }

    @Data
    public static class Tag {
        private String category;
        private String internal_name;
        private String localized_category_name;
        private String localized_tag_name;
        private String color;
    }

    @Getter
    public static class PriceUpdate {
        private String date;
        private int timezone_type;
        private String timezone;
    }

    @Data
    public static class InspectLinkParsed {
        private String a;
        private String s;
        private String d;
    }

    @Data
    public static class Description {
        private String type;
        private String value;
        private String color;
    }
}
