package com.soprasteria.simplejavaee;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import simplejavaee.generated.model.SampleModelData;
import simplejavaee.generated.model.TodoItemDto;

import java.util.List;

@Path("/todos")
public class TodoApi {
    @GET
    @Produces("application/json")
    public List<TodoItemDto> listTodos() {
        return new SampleModelData(System.currentTimeMillis()).sampleListOfTodoItemDto();
    }
}
