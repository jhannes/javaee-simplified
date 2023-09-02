package com.soprasteria.simplejavaee;

import com.soprasteria.infrastructure.ContentServlet;
import com.soprasteria.infrastructure.WebJarServlet;
import com.soprasteria.simplejavaee.api.TodoController;
import com.soprasteria.simplejavaee.api.TodoDao;
import jakarta.servlet.DispatcherType;
import no.soprasteria.generated.simplejavaee.model.SampleModelData;
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

        dataSource.setURL(System.getenv().getOrDefault("JDBC_URL", "jdbc:postgresql://localhost/postgres"));
        dataSource.setUser(System.getenv().getOrDefault("JDBC_USER", "postgres"));
        dataSource.setPassword(System.getenv().getOrDefault("JDBC_PASSWORD", "postgres"));

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
        try (var ignored = dbContext.startConnection(dataSource)) {
            var todoDao = new TodoDao(dbContext);
            if (todoDao.list().isEmpty()) {
                for (var todo : new SampleModelData(-1).sampleListOfTodoDto()) {
                    todoDao.save(todo);
                }

            }
        }

        server.start();
        log.info("Started server http://localhost:{}", server.getURI().getPort());
    }
}
