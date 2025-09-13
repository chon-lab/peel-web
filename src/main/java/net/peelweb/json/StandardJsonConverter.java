package net.peelweb.json;

import net.peelweb.exception.IncompatibleJsonFormatException;
import net.peelweb.exception.InvalidJsonFormatException;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StandardJsonConverter implements JsonConverter {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_INSTANT;

    private Object parseValue(String value, Class<?> fieldType) {
        if (fieldType == String.class) {
            return value.replaceAll("^\"|\"$", "").replaceAll("\\\\", "");
        } else if (fieldType == int.class || fieldType == Integer.class) {
            return Integer.parseInt(value);
        } else if (fieldType == long.class || fieldType == Long.class) {
            return Long.parseLong(value);
        } else if (fieldType == double.class || fieldType == Double.class) {
            return Double.parseDouble(value);
        } else if (fieldType == boolean.class || fieldType == Boolean.class) {
            return Boolean.parseBoolean(value);
        } else if (fieldType.isArray()) {
            return parseArray(value, fieldType.getComponentType());
        } else if (List.class.isAssignableFrom(fieldType)) {
            return parseList(value);
        } else {
            return this.fromJson(value, fieldType);
        }
    }

    private Object parseArray(String value, Class<?> componentType) {
        if (!value.startsWith("[") || !value.endsWith("]")) {
            throw new InvalidJsonFormatException("Invalid JSON array format");
        }

        value = value.substring(1, value.length() - 1).trim();
        List<String> elements = splitArrayElements(value);

        Object array = Array.newInstance(componentType, elements.size());
        for (int i = 0; i < elements.size(); i++) {
            Array.set(array, i, parseValue(elements.get(i).trim(), componentType));
        }

        return array;
    }

    private List<Object> parseList(String value) {
        if (!value.startsWith("[") || !value.endsWith("]")) {
            throw new InvalidJsonFormatException("Invalid JSON array format");
        }

        value = value.substring(1, value.length() - 1).trim();
        List<String> elements = splitArrayElements(value);

        List<Object> list = new ArrayList<>();
        for (String element : elements) {
            list.add(parseValue(element.trim(), String.class));
        }

        return list;
    }

    private List<String> splitKeyValuePairs(String json) {
        List<String> pairs = new ArrayList<>();
        int braceLevel = 0;
        int bracketLevel = 0;
        boolean inQuotes = false;
        boolean escapeNext = false;
        StringBuilder currentPair = new StringBuilder();

        for (char c : json.toCharArray()) {
            if (escapeNext) {
                // Adiciona o caractere escapado e ignora seu efeito
                currentPair.append(c);
                escapeNext = false;
                continue;
            }

            if (c == '\\') {
                // Indica que o próximo caractere será escapado
                escapeNext = true;
                currentPair.append(c);
                continue;
            }

            if (c == '"') {
                // Alterna o estado de `inQuotes` apenas se não estiver escapado
                inQuotes = !inQuotes;
            }

            if (!inQuotes) {
                switch (c) {
                    case '{':
                        braceLevel++;
                        break;
                    case '}':
                        braceLevel--;
                        break;
                    case '[':
                        bracketLevel++;
                        break;
                    case ']':
                        bracketLevel--;
                        break;
                    case ',':
                        // Apenas adiciona o par se fora de chaves, colchetes e strings
                        if (braceLevel == 0 && bracketLevel == 0) {
                            pairs.add(currentPair.toString().trim());
                            currentPair.setLength(0);
                            continue;
                        }
                        break;
                }
            }

            currentPair.append(c);
        }

        if (!currentPair.toString().isEmpty()) {
            pairs.add(currentPair.toString().trim());
        }

        return pairs;
    }

    private List<String> splitArrayElements(String array) {
        List<String> elements = new ArrayList<>();
        int bracketLevel = 0;
        boolean inQuotes = false;
        boolean escapeNext = false;
        StringBuilder currentElement = new StringBuilder();

        for (char c : array.toCharArray()) {
            if (escapeNext) {
                // Adiciona o caractere escapado e ignora seu efeito
                currentElement.append(c);
                escapeNext = false;
                continue;
            }

            if (c == '\\') {
                // Indica que o próximo caractere será escapado
                escapeNext = true;
                currentElement.append(c);
                continue;
            }

            if (c == '"') {
                // Alterna o estado de `inQuotes` apenas se não estiver escapado
                inQuotes = !inQuotes;
            }

            if (!inQuotes) {
                if (c == ',' && bracketLevel == 0) {
                    // Adiciona o elemento atual e inicia um novo
                    elements.add(currentElement.toString().trim());
                    currentElement.setLength(0);
                    continue;
                } else if (c == '[') {
                    bracketLevel++;
                } else if (c == ']') {
                    bracketLevel--;
                }
            }

            // Adiciona o caractere ao elemento atual
            currentElement.append(c);
        }

        if (!currentElement.toString().isEmpty()) {
            // Adiciona o último elemento restante
            elements.add(currentElement.toString().trim());
        }

        return elements;
    }

    private String serializeObject(Object obj) {
        StringBuilder json = new StringBuilder();
        json.append("{");

        boolean firstField = true;
        Class<?> currentClass = obj.getClass();

        while (currentClass != null) {
            Field[] fields = currentClass.getDeclaredFields();

            for (Field field : fields) {
                field.setAccessible(true);

                Object value;
                try {
                    value = field.get(obj);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }

                if (value == null) {
                    continue;
                }

                if (!firstField) {
                    json.append(",");
                }

                json.append("\"").append(field.getName()).append("\":");
                json.append(serializeValue(value));

                firstField = false;
            }

            currentClass = currentClass.getSuperclass();
        }

        json.append("}");
        return json.toString();
    }

    private String serializeArray(Object array) {
        StringBuilder json = new StringBuilder();
        json.append("[");

        int length = Array.getLength(array);
        for (int i = 0; i < length; i++) {
            if (i > 0) {
                json.append(",");
            }
            json.append(serializeValue(Array.get(array, i)));
        }

        json.append("]");
        return json.toString();
    }

    private String serializeValue(Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof String) {
            return "\"" + this.escapeString((String) value) + "\"";
        } else if (value instanceof Number || value instanceof Boolean) {
            return value.toString();
        } else if (value instanceof List) {
            return serializeArray(((List<?>) value).toArray());
        } else if (value.getClass().isArray()) {
            return serializeArray(value);
        } else if (value instanceof Date) {
            return "\"" + DATE_FORMAT.format(((Date) value).toInstant()) + "\"";
        } else {
            return serializeObject(value);
        }
    }

    private String escapeString(String str) {
        return str.replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }

    @Override
    public String toJson(Object o) {
        return serializeValue(o);
    }

    @Override
    public <T> T fromJson(String json, Class<T> clazz) {
        T instance;
        try {
            instance = clazz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        json = json.trim();
        if (!json.startsWith("{") || !json.endsWith("}")) {
            throw new InvalidJsonFormatException("Invalid JSON format");
        }

        json = json.substring(1, json.length() - 1).trim();

        List<String> keyValuePairs = splitKeyValuePairs(json);

        for (String pair : keyValuePairs) {
            String[] keyValue = pair.split(":", 2);
            if (keyValue.length != 2) {
                continue;
            }

            String key = keyValue[0].trim().replaceAll("^\"|\"$", "");
            String value = keyValue[1].trim();

            Field field;
            try {
                field = clazz.getDeclaredField(key);
            } catch (NoSuchFieldException e) {
                throw new IncompatibleJsonFormatException();
            }

            field.setAccessible(true);
            Object parsedValue = parseValue(value, field.getType());
            try {
                field.set(instance, parsedValue);
            } catch (IllegalArgumentException e) {
                throw new IncompatibleJsonFormatException();
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        return instance;
    }

}
