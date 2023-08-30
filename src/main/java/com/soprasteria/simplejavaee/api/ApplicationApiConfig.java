package com.soprasteria.simplejavaee.api;

import org.glassfish.jersey.server.ResourceConfig;

public class ApplicationApiConfig extends ResourceConfig {
    public ApplicationApiConfig() {
        register(TodoController.class);
    }
}
