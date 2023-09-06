package com.soprasteria.simplejavaee;

import com.soprasteria.infrastructure.ContentServlet;
import com.soprasteria.infrastructure.WebJarServlet;
import jakarta.servlet.DispatcherType;
import org.actioncontroller.jakarta.ApiJakartaServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.fluentjdbc.DbContext;
import org.postgresql.ds.PGSimpleDataSource;

import java.util.EnumSet;
import java.util.Optional;

public class ApplicationServer {

    private final Server server;
    private DbContext dbContext = new DbContext();

    public ApplicationServer(int port) {
        this.server = new Server(port);

        var dataSource = new PGSimpleDataSource();
        dataSource.setURL(Optional.ofNullable(System.getenv("JDBC_URL"))
                .orElse("jdbc:postgresql://localhost:5432/postgres"));
        dataSource.setUser("postgres");
        dataSource.setPassword("postgres");

        var context = new ServletContextHandler();
        context.addFilter(new FilterHolder(
                new ApplicationFilter(dbContext, dataSource)
        ), "/*", EnumSet.of(DispatcherType.REQUEST));
        context.addServlet(new ServletHolder(new ApiJakartaServlet(
                new TodoController(dbContext)
        )), "/api/*");
        context.addServlet(new ServletHolder(new WebJarServlet("swagger-ui")), "/api-doc/swagger-ui/*");
        context.addServlet(new ServletHolder(new ContentServlet("webapp")), "/*");
        this.server.setHandler(context);
    }

    public static void main(String[] args) throws Exception {
        new ApplicationServer(8080).start();
    }

    private void start() throws Exception {
        server.start();
    }

}
