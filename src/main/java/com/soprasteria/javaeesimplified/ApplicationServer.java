package com.soprasteria.javaeesimplified;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import java.io.IOException;

public class ApplicationServer {
    private final Server server;

    public ApplicationServer(int port) {
        var context = new ServletContextHandler();
        context.addServlet(new ServletHolder(new HttpServlet() {
            @Override
            protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
                resp.getWriter().write("Hello World");
            }
        }), "/api/*");

        server = new Server(port);
        server.setHandler(context);
    }

    public static void main(String[] args) throws Exception {
        new ApplicationServer(8080).start();
    }

    private void start() throws Exception {
        server.start();
    }
}
