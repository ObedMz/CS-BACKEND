package obed.org.apirest.util;

import obed.org.apirest.model.RawItemData;
import obed.org.apirest.model.StickerDTO;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class StickerMapper {
    public static List<StickerDTO> parserSticker(Set<RawItemData.Description> tags) {
        return tags.stream()
                .filter(desc -> desc.getValue().startsWith("<br"))
                .findFirst()
                .map(desc -> {
                    List<String> urls = getURLPattern(desc.getValue());
                    List<String> names = getNamePattern(desc.getValue());
                    return IntStream.range(0, urls.size())
                            .mapToObj(i -> new StickerDTO(urls.get(i), names.get(i)))
                            .collect(Collectors.toList());
                })
                .orElse(Collections.emptyList());
    }

    private static List<String> getURLPattern(String value) {
        List<String> list = new ArrayList<>();
        Pattern pattern = Pattern.compile("<img[^>]+src\\s*=\\s*\"([^\"]+)\"[^>]*>");
        Matcher matcher = pattern.matcher(value);
        while (matcher.find()) {
            list.add(matcher.group(1));
        }
        return list;
    }

    private static List<String> getNamePattern(String value) {
        List<String> list = new ArrayList<>();
        Pattern pattern = Pattern.compile("(Sticker|Patch): (.*?)(?=<)");
        Matcher matcher = pattern.matcher(value);
        while (matcher.find()) {
            String stickerInfo = matcher.group(2);
            list.addAll(Arrays.asList(stickerInfo.split(", ")));
        }
        return list;
    }
}
