package obed.org.apirest.model.data;

import lombok.Data;

@Data
public class StickerDTO {
    private String url;
    private String name;

    public StickerDTO(String url, String name) {
        this.url = url;
        this.name = name;
    }
}
