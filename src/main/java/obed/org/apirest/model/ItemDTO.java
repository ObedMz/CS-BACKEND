package obed.org.apirest.model;

import lombok.Data;
import obed.org.apirest.util.StickerMapper;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;

@Data
public class ItemDTO {
    @Id
    private String id;
    private String displayName;
    private Boolean modified = false;
    private String group;
    private String name;
    private Float addedPercentage = 0f;
    private Boolean hidden = false;

    private String itemType;
    private Double price;
    private String color;
    private String borderColor;
    private String rarity;
    private String quality;
    private String image;
    private Boolean tradeable;
    private String inspectLink;
    private WearType wear;
    private Integer marketTradeableRestriction;
    private List<StickerDTO> stickerList = new ArrayList<>();

    public static ItemDTO createByRawItem(RawItemData rawItem) {
        ItemDTO item = new ItemDTO();
        item.setId(rawItem.getId());
        item.setDisplayName(rawItem.getMarketname());
        item.setGroup(rawItem.getItemgroup());
        item.setName(rawItem.getItemname());
        item.setItemType(rawItem.getItemtype());
        item.setPrice(rawItem.getPricereal());
        item.setColor(rawItem.getColor());
        item.setBorderColor(rawItem.getBordercolor());
        item.setRarity(rawItem.getRarity());
        item.setQuality(rawItem.getQuality());
        item.setImage(rawItem.getImage());
        item.setTradeable(rawItem.isTradable());
        item.setInspectLink(rawItem.getInspectlink());
        item.setWear(rawItem.getWear() != null ? WearType.valueOf(rawItem.getWear()) : WearType.none);
        item.setMarketTradeableRestriction(rawItem.getMarkettradablerestriction());
        item.setStickerList(StickerMapper.parserSticker(rawItem.getDescriptions()));
        return item;
    }
}
