package com.soprasteria.simplejavaee;

import com.soprasteria.simplejavaee.api.MyLittleServlet;
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
        this.server = new Server(port);

        var context = new ServletContextHandler();
        context.setBaseResource(Resource.newClassPathResource("/webapp"));
        context.addServlet(new ServletHolder(new MyLittleServlet()), "/api/*");
        context.addServlet(new ServletHolder(new DefaultServlet()), "/*");
        server.setHandler(context);
    }

    public static void main(String[] args) throws Exception {
        new ApplicationServer(8080).start();
    }

    private void start() throws Exception {
        server.start();
        log.info("Started server http://localhost:{}", server.getURI().getPort());
    }
}
