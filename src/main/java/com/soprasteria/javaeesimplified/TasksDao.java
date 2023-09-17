package com.soprasteria.javaeesimplified;

import com.soprasteria.generated.javaeesimplified.model.TodoDto;
import org.fluentjdbc.DbContext;
import org.fluentjdbc.DbContextTable;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class TasksDao {
    private final Map<UUID, TodoDto> tasks;
    private final DbContextTable table;

    public TasksDao(Map<UUID, TodoDto> tasks, DbContext dbContext) {
        this.tasks = tasks;
        this.table = dbContext.tableWithTimestamps("todos");
    }

    public Collection<TodoDto> listAll() {
        return tasks.values();
    }

    public void insert(TodoDto task) {
        tasks.put(task.getId(), task);
        table.insert()
                .setPrimaryKey("id", task.getId())
                .setField("title", task.getTitle())
                .setField("status", task.getStatus())
                .execute();
    }

    public Optional<TodoDto> retrieve(UUID id) {
        return Optional.ofNullable(tasks.get(id));
    }

    public void update(TodoDto task) {
        tasks.put(task.getId(), task);
        table.where("id", task.getId())
                .update()
                .setField("title", task.getTitle())
                .setField("status", task.getStatus())
                .execute();
    }
}
