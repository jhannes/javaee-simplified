package com.soprasteria.infrastructure;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

/**
 * This class provides a facade to System environment variables, with the following benefits:
 *
 * <ul>
 *     <li>The class can be instantiated with the values you want for testing purposes</li>
 *     <li>The values can be read from a `.env` environment file</li>
 * </ul>
 */
public class Environment {

    private final Map<String, String> environment;

    public Environment() {
        environment = new HashMap<>(System.getenv());
        environment.putAll(readVariablesFromFile(Path.of(".env")));
    }

    public Environment(Map<String, String> environment) {
        this.environment = environment;
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

    private static HashMap<String, String> readVariablesFromFile(Path file) {
        var variables = new HashMap<String, String>();
        if (Files.isRegularFile(file)) {
            try (var inputStream = Files.newInputStream(file)) {
                var properties = new Properties();
                properties.load(inputStream);
                properties.forEach((key, value) -> variables.put(key.toString(), value.toString()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return variables;
    }
}
