package com.soprasteria.javaeesimplified;

import com.soprasteria.generated.javaeesimplified.model.TodoDto;
import org.actioncontroller.jakarta.ApiJakartaServlet;

import java.util.List;
import java.util.Map;
import java.util.UUID;

class ApplicationApiServlet extends ApiJakartaServlet {

    public ApplicationApiServlet(Map<UUID, TodoDto> tasks) {
        super(List.of(new TasksController(tasks), new LoginController()));
    }
}
