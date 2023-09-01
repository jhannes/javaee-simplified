package com.soprasteria.simplejavaee;

import com.soprasteria.generated.simplejavaee.model.TodoDto;
import org.fluentjdbc.DbContext;
import org.fluentjdbc.DbContextTable;

import java.util.List;

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

    public List<TodoDto> list() {
        return table.orderedBy("title").list(row -> new TodoDto()
                .id(row.getUUID("id"))
                .title(row.getString("title"))
                .description(row.getString("description"))
                .state(row.getEnum(TodoDto.StateEnum.class, "state"))
        );
    }
}
