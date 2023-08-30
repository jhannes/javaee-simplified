package com.soprasteria.simplejavaee.api;

import com.soprasteria.generated.simplejavaee.model.SampleModelData;
import com.soprasteria.generated.simplejavaee.model.TodoDto;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

import java.util.List;

@Path("/todos")
public class TodoController {

    private final SampleModelData sampleModelData = new SampleModelData(-1);

    @GET
    public List<TodoDto> listTodos() {
        return sampleModelData.sampleListOfTodoDto();
    }

}
