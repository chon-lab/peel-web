package net.peelweb.json;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Testes da conversão JSON padrão (StandardJsonConverter)")
class StandardJsonConverterTest {

    private final StandardJsonConverter converter = new StandardJsonConverter();

    @Test
    @DisplayName("Deve serializar mapas preservando chaves, valores e nulos")
    void shouldSerializeMapIncludingNullValues() {
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("name", "Peel Web");
        values.put("description", null);

        String json = this.converter.toJson(values);

        assertEquals("{\"name\":\"Peel Web\",\"description\":null}", json);
    }

    @Test
    @DisplayName("Deve serializar campos nulos de objetos")
    void shouldSerializeObjectIncludingNullFields() {
        ExamplePayload payload = new ExamplePayload("Peel Web", null);

        String json = this.converter.toJson(payload);

        assertEquals("{\"name\":\"Peel Web\",\"description\":null}", json);
    }

    private static final class ExamplePayload {

        private final String name;

        private final String description;

        private ExamplePayload(String name, String description) {
            this.name = name;
            this.description = description;
        }
    }
}
