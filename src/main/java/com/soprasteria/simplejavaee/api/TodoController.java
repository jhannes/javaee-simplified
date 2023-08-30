package com.soprasteria.simplejavaee.api;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

import java.util.List;

@Path("/todos")
public class TodoController {

    @GET
    public String listTodos() {
        return List.of("Hello", "World").toString();
    }

}
