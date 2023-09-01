package com.soprasteria.simplejavaee.api.jaxrs;

import com.soprasteria.infrastructure.SQLExceptionMapper;
import com.soprasteria.simplejavaee.ApplicationConfig;
import org.fluentjdbc.DbContext;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;

import java.util.Map;

public class ApplicationApiConfig extends ResourceConfig {
    public ApplicationApiConfig(ApplicationConfig applicationConfig, DbContext dbContext) {
        setProperties(Map.of(
                "jersey.config.server.wadl.disableWadl", true
        ));
        register(LoginController.class);
        register(TodoController.class);
        register(SQLExceptionMapper.class);
        register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(applicationConfig).to(ApplicationConfig.class);
                bind(dbContext).to(DbContext.class);
            }
        });
    }
}
