package com.soprasteria.infrastructure;

import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.stream.JsonParser;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("rawtypes")
public class JsonbEnumDeserializer implements JsonbDeserializer<Enum> {
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
    private static List<Object> getValues(Type type) {
        try {
            var valuesMethod = ((Class) type).getMethod("values");
            var values = (Object[]) valuesMethod.invoke(null);
            return List.of(values);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
