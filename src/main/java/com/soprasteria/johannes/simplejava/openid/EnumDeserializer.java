package com.soprasteria.johannes.simplejava.openid;

import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.stream.JsonParser;
import lombok.SneakyThrows;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

/**
 * Deserialization for enums. OpenAPI supports enums with spaces and other characters not permitted in Java enum names
 */
@SuppressWarnings("rawtypes")
public class EnumDeserializer implements JsonbDeserializer<Enum> {
    @Override
    public Enum deserialize(JsonParser parser, DeserializationContext ctx, Type type) {
        var values = getValues(type);
        for (Object value : values) {
            if (Objects.equals(value.toString(), parser.getString())) {
                return (Enum) value;
            }
        }
        throw new IllegalArgumentException("Can't find " + parser.getString() + " in " + values);
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    private static List<Object> getValues(Type type) {
        var valuesMethod = ((Class) type).getMethod("values");
        var values = (Object[]) valuesMethod.invoke(null);
        return List.of(values);
    }
}
