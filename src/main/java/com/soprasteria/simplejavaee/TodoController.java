package com.soprasteria.simplejavaee;

import com.soprasteria.generated.simplejavaee.model.TodoDto;
import org.actioncontroller.actions.GET;
import org.actioncontroller.optional.json.Json;
import org.fluentjdbc.DbContext;

import java.util.List;

public class TodoController {

    private TodoDao todoDao;

    public TodoController(DbContext dbContext) {
        todoDao = new TodoDao(dbContext);
    }

    @GET("/todos")
    @Json
    public List<TodoDto> listTodos() {
        return todoDao.list();
    }

}
