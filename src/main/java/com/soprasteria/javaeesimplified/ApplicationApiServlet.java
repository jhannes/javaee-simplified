package com.soprasteria.javaeesimplified;

import org.actioncontroller.jakarta.ApiJakartaServlet;
import org.fluentjdbc.DbContext;

import java.util.List;

class ApplicationApiServlet extends ApiJakartaServlet {

    public ApplicationApiServlet(DbContext dbContext) {
        super(List.of(new TasksController(dbContext), new LoginController()));
    }
}
