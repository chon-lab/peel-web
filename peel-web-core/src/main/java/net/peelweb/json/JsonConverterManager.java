package net.peelweb.json;

import lombok.Getter;

public class JsonConverterManager {

    @Getter
    public static JsonConverterManager instance = new JsonConverterManager();

    @Getter
    private final JsonConverter converter;

    private JsonConverterManager() {
        this.converter = new StandardJsonConverter();
    }

}
