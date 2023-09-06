package com.soprasteria.simplejavaee;

import com.soprasteria.generated.simplejavaee.model.TodoDto;
import org.fluentjdbc.DbContext;
import org.fluentjdbc.DbContextTable;

import java.util.List;

public class TodoDao {

    private final DbContextTable table;

    public TodoDao(DbContext dbContext) {
        table = dbContext.tableWithTimestamps("todos");
    }

    public List<TodoDto> list() {
        return table.query().list(row -> new TodoDto()
                .title(row.getString("title"))
                .status(row.getEnum(TodoDto.StatusEnum.class, "state")));
    }
}
