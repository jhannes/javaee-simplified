package com.soprasteria.johannes.simplejava;

import com.soprasteria.johannes.simplejava.api.ApiConfiguration;
import com.soprasteria.johannes.simplejava.server.ContentServlet;
import com.soprasteria.johannes.simplejava.server.WebjarServlet;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

import java.net.URI;

@Slf4j
public class ApplicationServer {

    private final Server server;

    public ApplicationServer(int port) {
        server = new Server(port);
        var context = new ServletContextHandler();
        context.addServlet(new ServletHolder(new ServletContainer(new ApiConfiguration())), "/api/*");
        context.addServlet(new ServletHolder(new WebjarServlet("swagger-ui")), "/api-doc/swagger-ui/*");
        context.addServlet(new ServletHolder(new ContentServlet("/webapp")), "/*");
        server.setHandler(context);
    }

    public void start() throws Exception {
        server.start();
    }

    public URI getURI() {
        return server.getURI();
    }

    public static void main(String[] args) throws Exception {
        var server = new ApplicationServer(8080);
        server.start();
        log.info("Started on {}", server.getURI());
    }
}
