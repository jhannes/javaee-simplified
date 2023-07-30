package com.soprasteria.johannes.simplejava;

import com.soprasteria.johannes.simplejava.server.ContentServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import java.net.URI;

public class ApplicationServer {

    private final Server server;

    public ApplicationServer(int port) {
        server = new Server(port);
        var context = new ServletContextHandler();
        context.addServlet(new ServletHolder(new ContentServlet()), "/*");
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
        System.out.println("Started on " + server.getURI());
    }
}
