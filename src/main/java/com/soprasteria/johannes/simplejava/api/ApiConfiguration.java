package com.soprasteria.johannes.simplejava.api;

import com.soprasteria.johannes.simplejava.ApplicationConfig;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;

import java.util.Map;

public class ApiConfiguration extends ResourceConfig {
    public ApiConfiguration(final ApplicationConfig config) {
        setProperties(Map.of("jersey.config.server.wadl.disableWadl", "true"));
        register(LoginController.class);

        register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(config).to(ApplicationConfig.class);
            }
        });
    }
}
