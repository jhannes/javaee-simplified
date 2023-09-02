package com.soprasteria.simplejavaee.api;

import no.soprasteria.generated.simplejavaee.model.SampleModelData;
import no.soprasteria.generated.simplejavaee.model.TodosGet200ResponseDto;
import org.actioncontroller.actions.GET;
import org.actioncontroller.optional.json.Json;

import java.util.List;

public class TodoController {
    @GET("/todos")
    @Json
    public List<TodosGet200ResponseDto> listTodos() {
        return new SampleModelData(System.currentTimeMillis()).sampleListOfTodosGet200ResponseDto();
    }
}
