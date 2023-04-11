package com.soprasteria.simplejavaee;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import simplejavaee.generated.model.SampleModelData;
import simplejavaee.generated.model.TodoItemDto;

import java.util.List;

@Path("")
public class TodoApi {
    @GET
    public List<TodoItemDto> listTodos() {
        return new SampleModelData(System.currentTimeMillis()).sampleListOfTodoItemDto();
    }
}
