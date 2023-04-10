package com.soprasteria.simplejavaee;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.WebAppContext;

public class JavaZoneDemoServer {

    private final Server server = new Server(8080);

    public JavaZoneDemoServer() {
        var handler = new WebAppContext(Resource.newClassPathResource("/webapp"), "/");
        server.setHandler(handler);
    }

    public static void main(String[] args) throws Exception {
        new JavaZoneDemoServer().start();
    }

    private void start() throws Exception {
        server.start();
    }

}
