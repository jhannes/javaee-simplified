package com.soprasteria.simplejavaee;

import com.soprasteria.infrastructure.ContentServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class ApplicationServer {
    private final Server server;

    public ApplicationServer(int port) {
        this.server = new Server(port);

        var context = new ServletContextHandler();
        context.addServlet(new ServletHolder(new ContentServlet()), "/*");
        server.setHandler(context);
    }

    public static void main(String[] args) throws Exception {
        new ApplicationServer(21080).start();
    }

    private void start() throws Exception {
        server.start();
    }
}
