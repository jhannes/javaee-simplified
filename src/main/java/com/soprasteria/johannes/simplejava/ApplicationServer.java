package com.soprasteria.johannes.simplejava;

import com.soprasteria.johannes.infrastructure.Environment;
import com.soprasteria.johannes.simplejava.api.ApiConfiguration;
import com.soprasteria.johannes.simplejava.server.ContentServlet;
import com.soprasteria.johannes.simplejava.server.WebjarServlet;
import jakarta.servlet.DispatcherType;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

import java.net.URI;
import java.util.EnumSet;

@Slf4j
public class ApplicationServer {

    private final Server server;

    public ApplicationServer(int port, ApplicationConfig config) {
        server = new Server(port);
        var context = new ServletContextHandler();
        context.addServlet(new ServletHolder(new ServletContainer(new ApiConfiguration(config))), "/api/*");
        context.addServlet(new ServletHolder(new WebjarServlet("swagger-ui")), "/api-doc/swagger-ui/*");
        context.addServlet(new ServletHolder(new ContentServlet("/webapp")), "/*");
        context.addFilter(new FilterHolder(new ApplicationFilter()), "/*", EnumSet.of(DispatcherType.REQUEST));
        server.setHandler(context);
    }

    public void start() throws Exception {
        server.start();
    }

    @SneakyThrows
    public URI getURI() {
        return new URI("http://localhost:" + server.getURI().getPort());
    }

    @SneakyThrows
    public static ApplicationServer start(int port, Environment env) {
        var server = new ApplicationServer(port, new ApplicationConfig(env));
        server.start();
        return server;
    }

    public static void main(String[] args) throws Exception {
        var server = start(8080, new Environment());
        log.info("Started on {}", server.getURI());
    }
}
