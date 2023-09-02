package com.soprasteria.simplejavaee.api;

import no.soprasteria.generated.simplejavaee.model.SampleModelData;
import no.soprasteria.generated.simplejavaee.model.TodoDto;
import org.actioncontroller.actions.GET;
import org.actioncontroller.optional.json.Json;

import java.util.List;

public class TodoController {

    private final TodoDao todoDao = new TodoDao();

    @GET("/todos")
    @Json
    public List<TodoDto> listTodos() {
        return todoDao.list();
    }
}
