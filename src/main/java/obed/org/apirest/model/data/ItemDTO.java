package obed.org.apirest.model.data;

import lombok.Builder;
import lombok.Data;
import obed.org.apirest.util.StickerMapper;
import org.springframework.data.annotation.Id;

import java.util.List;

@Data
@Builder
public class ItemDTO {
    @Id
    private String id;
    private String displayName;
    private Boolean modified;
    private String group;
    private String name;
    private Float addedPercentage;
    private Boolean hidden;

    private String itemType;
    private Double price;
    private Double custom_price;
    private String color;
    private String borderColor;
    private String rarity;
    private String quality;
    private String image;
    private Boolean tradeable;
    private String inspectLink;
    private WearType wear;
    private Integer marketTradeableRestriction;
    private List<StickerDTO> stickerList;

    public static ItemDTO createByRawItem(RawItemData rawItem) {
        return ItemDTO.builder()
                .id(rawItem.getId())
                .displayName(rawItem.getMarketname())
                .group(rawItem.getItemgroup())
                .name(rawItem.getItemname())
                .itemType(rawItem.getItemtype())
                .price(rawItem.getPricereal())
                .color(rawItem.getColor())
                .borderColor(rawItem.getBordercolor())
                .rarity(rawItem.getRarity())
                .quality(rawItem.getQuality())
                .image(rawItem.getImage())
                .tradeable(rawItem.isTradable())
                .inspectLink(rawItem.getInspectlink())
                .wear(rawItem.getWear() != null ? WearType.valueOf(rawItem.getWear()) : WearType.none)
                .marketTradeableRestriction(rawItem.getMarkettradablerestriction())
                .stickerList(StickerMapper.parserSticker(rawItem.getDescriptions()))
                .build();
    }
}
