package com.soprasteria.simplejavaee;

import com.soprasteria.infrastructure.ContentServlet;
import com.soprasteria.infrastructure.Slf4jRequestLog;
import com.soprasteria.infrastructure.WebJarServlet;
import com.soprasteria.simplejavaee.api.LoginController;
import com.soprasteria.simplejavaee.api.TodoController;
import jakarta.servlet.DispatcherType;
import org.actioncontroller.jakarta.ApiJakartaServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.fluentjdbc.DbContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.EnumSet;
import java.util.List;

public class ApplicationServer {

    private static final Logger log = LoggerFactory.getLogger(ApplicationServer.class);

    private final Server server;
    private final ApplicationConfig applicationConfig = new ApplicationConfig();
    private final DbContext dbContext = new DbContext();

    private final DataSource dataSource = applicationConfig.createDataSource();

    private final ApplicationFilter applicationFilter = new ApplicationFilter(applicationConfig, dbContext, dataSource);

    public ApplicationServer(int port) {
        this.server = new Server(port);

        var context = new ServletContextHandler();
        context.addFilter(new FilterHolder(applicationFilter), "/*", EnumSet.of(DispatcherType.REQUEST));
        context.addServlet(new ServletHolder(new ApiJakartaServlet(List.of(
                new TodoController(dbContext), new LoginController(applicationConfig)
        ))), "/apis/*");
        context.addServlet(new ServletHolder(new WebJarServlet("swagger-ui")), "/api-doc/swagger-ui/*");
        context.addServlet(new ServletHolder(new ContentServlet("webapp")), "/*");
        server.setHandler(context);

        server.setRequestLog(new Slf4jRequestLog());
    }

    public static void main(String[] args) throws Exception {
        new ApplicationServer(8080).start();
    }

    private void start() throws Exception {
        server.start();
        log.info("Started on http://localhost:" + server.getURI().getPort());
    }
}
