package com.soprasteria.simplejavaee;

import org.glassfish.jersey.server.ResourceConfig;

public class ApplicationApiConfig extends ResourceConfig {
    public ApplicationApiConfig() {
        register(TodosController.class);
    }
}
