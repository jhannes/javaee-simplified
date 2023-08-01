package com.soprasteria.johannes.infrastructure;

import lombok.SneakyThrows;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;

public class Environment {

    private final Map<String, String> environment;

    @SneakyThrows(IOException.class)
    public Environment() {
        this(new HashMap<>(System.getenv()));
        var dotenv = Path.of(".env");
        if (Files.exists(dotenv)) {
            Properties properties = new Properties();
            try (var input = Files.newInputStream(dotenv)) {
                properties.load(input);
            }
            properties.forEach((k,v) -> environment.put((String) k, (String) v));
        }
    }

    public Environment(Map<String, String> environment) {
        this.environment = environment;
    }

    @SneakyThrows
    public URI getURI(String key, String defaultValue) {
        return new URI(get(key, defaultValue));
    }

    private String get(String key, String defaultValue) {
        return environment.getOrDefault(key, defaultValue);
    }

    public String get(String key) {
        if (!environment.containsKey(key)) {
            throw new NoSuchElementException("Missing ENVIRONMENT property " + key);
        }
        return environment.get(key);
    }
}
