package com.soprasteria.infrastructure;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

public class Environment {

    private final Map<String, String> environment = new HashMap<>(System.getenv());

    public Environment() {
        var dotenv = Path.of(".env");
        if (Files.isRegularFile(dotenv)) {
            try (var inputStream = Files.newInputStream(dotenv)) {
                var properties = new Properties();
                properties.load(inputStream);
                properties.forEach((key, value) -> environment.put(key.toString(), value.toString()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public String get(String key) {
        return optional(key).orElseThrow(() -> new RuntimeException("Missing environment variable " + key));
    }

    public Optional<String> optional(String key) {
        return Optional.ofNullable(environment.get(key));
    }

    public String get(String key, String defaultValue) {
        return optional(key).orElse(defaultValue);
    }
}
