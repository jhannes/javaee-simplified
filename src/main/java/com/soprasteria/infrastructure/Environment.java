package com.soprasteria.infrastructure;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Environment {

    private final Map<String, String> environment = new HashMap<>(System.getenv());

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
