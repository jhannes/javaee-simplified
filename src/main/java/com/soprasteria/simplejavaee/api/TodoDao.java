package com.soprasteria.simplejavaee.api;

import no.soprasteria.generated.simplejavaee.model.SampleModelData;
import no.soprasteria.generated.simplejavaee.model.TodoDto;

import java.util.List;

public class TodoDao {


    public List<TodoDto> list() {
        return new SampleModelData(System.currentTimeMillis()).sampleListOfTodoDto();
    }
}
