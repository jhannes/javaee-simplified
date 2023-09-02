package com.soprasteria.simplejavaee;

import com.soprasteria.infrastructure.ContentServlet;
import com.soprasteria.infrastructure.WebJarServlet;
import com.soprasteria.simplejavaee.api.TodoController;
import jakarta.servlet.DispatcherType;
import org.actioncontroller.jakarta.ApiJakartaServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.fluentjdbc.DbContext;
import org.flywaydb.core.Flyway;
import org.postgresql.ds.PGSimpleDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumSet;

public class ApplicationServer {

    private static final Logger log = LoggerFactory.getLogger(ApplicationServer.class);
    private final Server server;
    private final DbContext dbContext = new DbContext();
    private final PGSimpleDataSource dataSource = new PGSimpleDataSource();

    public ApplicationServer(int port) {
        this.server = new Server(port);

        dataSource.setUser("postgres");

        Flyway.configure().dataSource(dataSource).load().migrate();


        var context = new ServletContextHandler();
        context.addFilter(new FilterHolder(new ApplicationFilter(dbContext, dataSource)), "/*", EnumSet.of(DispatcherType.REQUEST));
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
