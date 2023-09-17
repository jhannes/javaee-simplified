package com.soprasteria.javaeesimplified;

import com.soprasteria.generated.javaeesimplified.model.TaskStatusDto;
import com.soprasteria.generated.javaeesimplified.model.TodoDto;
import org.fluentjdbc.DatabaseRow;
import org.fluentjdbc.DbContext;
import org.fluentjdbc.DbContextTable;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public class TasksDao {
    private final DbContextTable table;

    public TasksDao(DbContext dbContext) {
        this.table = dbContext.tableWithTimestamps("todos");
    }

    public Collection<TodoDto> listAll() {
        return table.query().list(TasksDao::readTodo);
    }

    public void insert(TodoDto task) {
        table.insert()
                .setPrimaryKey("id", task.getId())
                .setField("title", task.getTitle())
                .setField("status", task.getStatus())
                .execute();
    }

    public Optional<TodoDto> retrieve(UUID id) {
        return table.where("id", id).singleObject(TasksDao::readTodo);
    }

    public void update(TodoDto task) {
        table.where("id", task.getId())
                .update()
                .setField("title", task.getTitle())
                .setField("status", task.getStatus())
                .execute();
    }

    private static TodoDto readTodo(DatabaseRow row) throws SQLException {
        return new TodoDto()
                .id(row.getUUID("id"))
                .title(row.getString("title"))
                .status(row.getEnum(TaskStatusDto.class, "status"));
    }

}
