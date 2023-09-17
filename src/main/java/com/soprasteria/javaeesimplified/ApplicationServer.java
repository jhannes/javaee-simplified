package com.soprasteria.javaeesimplified;

import com.soprasteria.infrastructure.WebJarServlet;
import jakarta.servlet.DispatcherType;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.fluentjdbc.DbContext;
import org.flywaydb.core.Flyway;
import org.postgresql.ds.PGSimpleDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.EnumSet;

public class ApplicationServer {
    private static final Logger log = LoggerFactory.getLogger(ApplicationServer.class);
    private final Server server;
    private final DbContext dbContext = new DbContext();

    public ApplicationServer(int port, DataSource dataSource) {
        var context = new ServletContextHandler();
        context.setBaseResource(Resource.newClassPathResource("webapp"));
        context.addServlet(new ServletHolder(new DefaultServlet()), "/*");
        context.addServlet(new ServletHolder(new WebJarServlet("swagger-ui")), "/api-doc/swagger-ui/*");
        context.addServlet(new ServletHolder(new ApplicationApiServlet(dbContext)), "/api/*");
        context.addFilter(new FilterHolder(new ApplicationServletFilter(dbContext, dataSource)), "/*", EnumSet.of(DispatcherType.REQUEST));

        server = new Server(port);
        server.setHandler(context);
    }

    public static void main(String[] args) throws Exception {
        var dataSource = new PGSimpleDataSource();
        dataSource.setUser("postgres");
        Flyway.configure().dataSource(dataSource).load().migrate();
        new ApplicationServer(8080, dataSource).start();
    }

    private void start() throws Exception {
        server.start();
        log.info("Started on http://localhost:{}", server.getURI().getPort());
    }
}
