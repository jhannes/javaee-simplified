package com.soprasteria.simplejavaee;

import com.soprasteria.infrastructure.ContentServlet;
import com.soprasteria.infrastructure.WebJarServlet;
import com.soprasteria.simplejavaee.api.TodoController;
import org.actioncontroller.jakarta.ApiJakartaServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.fluentjdbc.DbContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationServer {

    private static final Logger log = LoggerFactory.getLogger(ApplicationServer.class);
    private final Server server;
    private DbContext dbContext = new DbContext();

    public ApplicationServer(int port) {
        this.server = new Server(port);

        var context = new ServletContextHandler();
        context.addServlet(new ServletHolder(new ApiJakartaServlet(
                new TodoController(dbContext)
        )), "/api/*");
        context.addServlet(new ServletHolder(new WebJarServlet("swagger-ui")), "/api-doc/swagger-ui/*");
        context.addServlet(new ServletHolder(new ContentServlet("webapp")), "/*");
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
