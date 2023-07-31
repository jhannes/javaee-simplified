package com.soprasteria.johannes.simplejava.api;

import org.glassfish.jersey.server.ResourceConfig;

public class ApiConfiguration extends ResourceConfig {
    public ApiConfiguration() {
        register(LoginController.class);
    }
}
