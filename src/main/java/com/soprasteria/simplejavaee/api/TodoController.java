package com.soprasteria.simplejavaee.api;

import org.actioncontroller.actions.GET;
import org.actioncontroller.values.ContentBody;

public class TodoController {
    @GET("/todos")
    @ContentBody(contentType = "application/json")
    public String listTodos() {
        return """
                ["give talk", "prepare talk"]
                """;
    }
}
