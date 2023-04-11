package com.soprasteria.simplejavaee;

import org.eclipse.jetty.server.CustomRequestLog;
import org.eclipse.jetty.server.ResourceService;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

public class JavaZoneDemoServer {

    private final Server server = new Server(8080);

    public JavaZoneDemoServer() {
        var handler = new ServletContextHandler();
        handler.setBaseResource(Resource.newClassPathResource("/webapp"));
        handler.addServlet(new ServletHolder(new DefaultServlet(new ResourceService())), "/*");
        handler.addServlet(new ServletHolder(new WebJarServlet("swagger-ui")), "/api-doc/swagger-ui/*");
        handler.addServlet(new ServletHolder(new ServletContainer(new ResourceConfig(TodoApi.class))), "/api/*");
        server.setHandler(handler);
        server.setRequestLog(new CustomRequestLog());
    }

    public static void main(String[] args) throws Exception {
        new JavaZoneDemoServer().start();
    }

    private void start() throws Exception {
        server.start();
    }

}
