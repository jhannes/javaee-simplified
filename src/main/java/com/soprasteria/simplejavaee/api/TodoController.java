package com.soprasteria.simplejavaee.api;

import com.soprasteria.generated.simplejavaee.model.TodoDto;
import com.soprasteria.simplejavaee.TodoDao;
import org.actioncontroller.actions.GET;
import org.actioncontroller.actions.POST;
import org.actioncontroller.optional.json.Json;
import org.fluentjdbc.DbContext;

import java.util.List;

public class TodoController {
    private final TodoDao dao;

    public TodoController(DbContext dbContext) {
        this.dao = new TodoDao(dbContext);
    }

    @GET("/todos")
    @Json
    public List<TodoDto> listTodos() {
        return dao.list();
    }

    @POST("/todos")
    public void newTodo(@Json TodoDto newTodo) {
        dao.save(newTodo);
    }
}
