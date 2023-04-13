package com.soprasteria.simplejavaee;

import jakarta.servlet.DispatcherType;
import org.eclipse.jetty.server.CustomRequestLog;
import org.eclipse.jetty.server.ResourceService;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.flywaydb.core.Flyway;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.postgresql.ds.PGSimpleDataSource;

import java.util.EnumSet;

public class JavaZoneDemoServer {

    private final PGSimpleDataSource dataSource = new PGSimpleDataSource();
    {
        dataSource.setURL("jdbc:postgresql://localhost:5432/postgres");
        dataSource.setUser("postgres");
    }


    private final Server server = new Server(8080);

    public JavaZoneDemoServer() {
        var handler = new ServletContextHandler();
        handler.setBaseResource(Resource.newClassPathResource("/webapp"));
        handler.addFilter(new FilterHolder(new RequestContextFilter()), "/*", EnumSet.of(DispatcherType.REQUEST));
        handler.addFilter(new FilterHolder(new ApplicationFilter(dataSource)), "/api/*", EnumSet.of(DispatcherType.REQUEST));
        handler.addServlet(new ServletHolder(new DefaultServlet(new ResourceService())), "/*");
        handler.addServlet(new ServletHolder(new ServletContainer(getResourceConfig())), "/api/*");
        handler.addServlet(new ServletHolder(new WebJarServlet("swagger-ui")), "/swagger-ui/swagger-ui/*");
        server.setHandler(handler);
        server.setRequestLog(new CustomRequestLog());
    }

    private static ResourceConfig getResourceConfig() {
        var resourceConfig = new ResourceConfig(TodoApi.class);
        resourceConfig.register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(new ApplicationEmailServiceImplementation()).to(ApplicationEmailService.class);
            }
        });
        return resourceConfig;
    }

    public static void main(String[] args) throws Exception {
        new JavaZoneDemoServer().start();
    }

    private void start() throws Exception {
        Flyway.configure().dataSource(dataSource).load().migrate();

        server.start();
    }

}
