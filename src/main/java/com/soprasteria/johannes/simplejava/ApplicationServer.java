package com.soprasteria.johannes.simplejava;

import org.eclipse.jetty.server.Server;

import java.net.URI;

public class ApplicationServer {

    private final Server server;

    public ApplicationServer(int port) {
        server = new Server(port);
    }

    public void start() throws Exception {
        server.start();
    }

    public URI getURI() {
        return server.getURI();
    }

    public static void main(String[] args) throws Exception {
        new ApplicationServer(8080).start();
    }
}
