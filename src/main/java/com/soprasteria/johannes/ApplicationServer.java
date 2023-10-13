package com.soprasteria.johannes;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationServer {

    private final Server server;

    private static final Logger log = LoggerFactory.getLogger(ApplicationServer.class);

    public ApplicationServer(int port) {
        server = new Server(port);

        var handler = new ServletContextHandler();
        handler.setBaseResource(Resource.newClassPathResource("/webapp"));
        handler.addServlet(new ServletHolder(new MyLittleServlet()), "/api/*");
        handler.addServlet(new ServletHolder(new DefaultServlet()), "/*");
        server.setHandler(handler);
    }


    public static void main(String[] args) throws Exception {
        new ApplicationServer(11080).start();
    }

    private void start() throws Exception {
        server.start();
        log.info("server started at http://localhost:{}", server.getURI().getPort());
    }
}
