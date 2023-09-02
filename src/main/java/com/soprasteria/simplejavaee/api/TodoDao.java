package com.soprasteria.simplejavaee.api;

import no.soprasteria.generated.simplejavaee.model.TodoDto;
import org.fluentjdbc.DbContext;
import org.fluentjdbc.DbContextTable;

import java.util.List;

public class TodoDao {


    private DbContextTable todos;

    public TodoDao(DbContext dbContext) {
        todos = dbContext.tableWithTimestamps("todos");
    }

    public List<TodoDto> list() {
        return todos
                .orderedBy("updated_at")
                .list(row -> new TodoDto()
                        .title(row.getString("title"))
                        .state(row.getEnum(TodoDto.StateEnum.class, "state")))
        ;
    }
}
