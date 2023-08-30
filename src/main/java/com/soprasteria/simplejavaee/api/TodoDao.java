package com.soprasteria.simplejavaee.api;

import com.soprasteria.generated.simplejavaee.model.TodoDto;
import org.fluentjdbc.DbContext;
import org.fluentjdbc.DbContextTable;

public class TodoDao {

    private final DbContextTable table;

    public TodoDao(DbContext dbContext) {
        this.table = dbContext.table("todos");
    }

    public void save(TodoDto todo) {
        table.newSaveBuilderWithUUID("id", todo.getId())
                .setField("title", todo.getTitle())
                .setField("description", todo.getDescription())
                .setField("state", todo.getState())
                .execute();
    }
}
