package com.soprasteria.simplejavaee;

import org.eclipse.jetty.server.Server;

public class ApplicationServer {
    private final Server server;

    public ApplicationServer(int port) {
        this.server = new Server(port);
    }

    public static void main(String[] args) throws Exception {
        new ApplicationServer(8080).start();
    }

    private void start() throws Exception {
        server.start();
    }
}
