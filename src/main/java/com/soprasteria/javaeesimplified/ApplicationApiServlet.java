package com.soprasteria.javaeesimplified;

import org.actioncontroller.jakarta.ApiJakartaServlet;

import java.util.List;

class ApplicationApiServlet extends ApiJakartaServlet {

    public ApplicationApiServlet(TasksDao tasksDao) {
        super(List.of(new TasksController(tasksDao), new LoginController()));
    }
}
