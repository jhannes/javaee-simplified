package com.soprasteria.simplejavaee.api;

import no.soprasteria.generated.simplejavaee.model.TodoDto;
import org.fluentjdbc.DbContext;
import org.fluentjdbc.DbContextTable;

import java.util.List;
import java.util.UUID;

public class TodoDao {
    
    private final DbContextTable table;

    public TodoDao(DbContext dbContext) {
        table = dbContext.tableWithTimestamps("todos");
    }

    public List<TodoDto> list() {
        return table
                .orderedBy("updated_at")
                .list(row -> new TodoDto()
                        .title(row.getString("title"))
                        .state(row.getEnum(TodoDto.StateEnum.class, "state")))
        ;
    }

    public void save(TodoDto todo) {
        table.newSaveBuilderWithUUID("id", UUID.randomUUID())
                .setField("title", todo.getTitle())
                .setField("state", todo.getState())
                .execute();
    }
}
