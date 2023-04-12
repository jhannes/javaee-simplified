package com.soprasteria.simplejavaee;

import com.soprasteria.simplejavaee.infrastructure.ApplicationDataSource;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import simplejavaee.generated.model.TodoItemDto;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@Path("/todos")
public class TodoApi {

    private ApplicationEmailService emailService;

    public TodoApi(ApplicationEmailService emailService) {
        this.emailService = emailService;
    }

    @GET
    @Produces("application/json")
    public List<TodoItemDto> listTodos() throws SQLException {
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
    @Consumes("application/json")
    public void saveTodoAndEmail(TodoItemDto todoItem) throws SQLException {
        saveTodo(todoItem);
        emailService.sendNewTodoEmail(todoItem);
    }
}
