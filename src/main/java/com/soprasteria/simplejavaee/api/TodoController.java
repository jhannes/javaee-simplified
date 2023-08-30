package com.soprasteria.simplejavaee.api;

import com.soprasteria.generated.simplejavaee.model.SampleModelData;
import com.soprasteria.generated.simplejavaee.model.TodoDto;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

import java.util.ArrayList;
import java.util.List;

@Path("/todos")
public class TodoController {

    private static final SampleModelData sampleModelData = new SampleModelData(-1);
    private static final List<TodoDto> todoDtos = new ArrayList<>(sampleModelData.sampleListOfTodoDto());

    @GET
    public List<TodoDto> listTodos() throws InterruptedException {
        Thread.sleep(500);
        return todoDtos;
    }

    @POST
    public void createTodo(TodoDto todo) throws InterruptedException {
        Thread.sleep(100);
        todoDtos.add(todo);
    }

}
