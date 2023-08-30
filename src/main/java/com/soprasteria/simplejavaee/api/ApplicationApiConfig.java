package com.soprasteria.simplejavaee.api;

import org.glassfish.jersey.server.ResourceConfig;

import java.util.Map;

public class ApplicationApiConfig extends ResourceConfig {
    public ApplicationApiConfig() {
        setProperties(Map.of(
                "jersey.config.server.wadl.disableWadl", true
        ));
        register(LoginController.class);
        register(TodoController.class);
    }
}
