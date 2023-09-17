package com.soprasteria.javaeesimplified;

import com.soprasteria.generated.javaeesimplified.model.TodoDto;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class TasksDao {
    private final Map<UUID, TodoDto> tasks;

    public TasksDao(Map<UUID, TodoDto> tasks) {
        this.tasks = tasks;
    }

    public Collection<TodoDto> listAll() {
        return tasks.values();
    }

    public void insert(TodoDto task) {
        tasks.put(task.getId(), task);
    }

    public Optional<TodoDto> retrieve(UUID id) {
        return Optional.ofNullable(tasks.get(id));
    }

    public void update(TodoDto task) {
        tasks.put(task.getId(), task);
    }
}
