package com.soprasteria.simplejavaee;

import com.soprasteria.simplejavaee.infrastructure.ApplicationDataSource;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simplejavaee.generated.model.TodoItemDto;

import java.security.Principal;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@Path("/todos")
public class TodoApi {

    private static final Logger log = LoggerFactory.getLogger(TodoApi.class);

    private final ApplicationEmailService emailService;
    private final Principal userPrincipal;

    @Inject
    public TodoApi(ApplicationEmailService emailService, @Context HttpServletRequest req) {
        this(emailService, req.getUserPrincipal());
    }

    public TodoApi(ApplicationEmailService emailService) {
        this(emailService, (Principal) null);
    }

    public TodoApi(ApplicationEmailService emailService, Principal userPrincipal) {
        this.emailService = emailService;
        this.userPrincipal = userPrincipal;
    }

    @GET
    @Produces("application/json")
    public List<TodoItemDto> listTodos() throws SQLException {
        log.info("Hello {}", userPrincipal);
        try (var statement = ApplicationDataSource.prepareStatement("select * from TODO_ITEMS")) {
            return ApplicationDataSource.queryAllRows(statement,
                    rs -> new TodoItemDto()
                            .id((UUID) rs.getObject("id"))
                            .title(rs.getString("title"))
                            .description(rs.getString("description"))
            );
        }
    }

    @POST
    @Consumes("application/json")
    public void saveTodo(TodoItemDto todoItem) throws SQLException {
        try (var statement = ApplicationDataSource.prepareStatement("insert into TODO_ITEMS (id, title, description) VALUES (?, ?, ?)")) {
            statement.setObject(1, todoItem.getId());
            statement.setString(2, todoItem.getTitle());
            statement.setString(3, todoItem.getDescription());
            statement.executeUpdate();
        }
    }

    @POST
    @Path("/withEmail")
    @Consumes("application/json")
    public void saveTodoAndEmail(TodoItemDto todoItem) throws SQLException {
        try (var tx = ApplicationDataSource.requireTransaction()) {
            saveTodo(todoItem);
            emailService.sendNewTodoEmail(todoItem);
            tx.setComplete();
        }
    }
}
