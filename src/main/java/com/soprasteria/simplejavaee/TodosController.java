package com.soprasteria.simplejavaee;

import com.soprasteria.generated.simplejavaee.model.SampleModelData;
import com.soprasteria.generated.simplejavaee.model.TodoDto;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

import java.util.List;

@Path("/todos")
public class TodosController {

    @GET
    public List<TodoDto> todos() {
        return new SampleModelData(System.currentTimeMillis()).sampleListOfTodoDto();
    }

}
