package com.soprasteria.javaeesimplified;

import com.soprasteria.infrastructure.WebJarServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationServer {
    private static final Logger log = LoggerFactory.getLogger(ApplicationServer.class);
    private final Server server;

    public ApplicationServer(int port) {
        var context = new ServletContextHandler();
        context.setBaseResource(Resource.newClassPathResource("webapp"));
        context.addServlet(new ServletHolder(new DefaultServlet()), "/*");
        context.addServlet(new ServletHolder(new WebJarServlet("swagger-ui")), "/api-doc/swagger-ui/*");
        context.addServlet(new ServletHolder(new ApplicationApiServlet()), "/api/*");

        server = new Server(port);
        server.setHandler(context);
    }

    public static void main(String[] args) throws Exception {
        new ApplicationServer(8080).start();
    }

    private void start() throws Exception {
        server.start();
        log.info("Started on http://localhost:{}", server.getURI().getPort());
    }
}
