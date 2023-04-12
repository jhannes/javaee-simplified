package com.soprasteria.simplejavaee;

import com.soprasteria.simplejavaee.infrastructure.ApplicationDataSource;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import simplejavaee.generated.model.TodoItemDto;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Path("/todos")
public class TodoApi {

    private final List<TodoItemDto> allTodos = new ArrayList<>();

    @GET
    @Produces("application/json")
    public List<TodoItemDto> listTodos() throws SQLException {
        try (var statement = ApplicationDataSource.prepareStatement("select * from TODO_ITEMS")) {
            //return ApplicationDataSource.queryAllRows(statement, rs -> new TodoItemDto());
        }

        return allTodos;
    }

    @POST
    @Consumes("application/json")
    public void saveTodo(TodoItemDto todoItem) throws SQLException {
        try (var statement = ApplicationDataSource.prepareStatement("insert into TODO_ITEMS (id, title, description) VALUES (?, ?, ?)")) {
            statement.setString(1, todoItem.getId().toString());
            statement.setString(2, todoItem.getTitle());
            statement.setString(3, todoItem.getDescription());
            statement.executeUpdate();
        }
        allTodos.add(todoItem);
    }
}
