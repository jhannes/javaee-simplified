package com.soprasteria.javaeesimplified;

import com.soprasteria.generated.javaeesimplified.model.TodoDto;
import com.soprasteria.generated.javaeesimplified.model.UpdateTaskStatusRequestDto;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public class TasksController {

    private final Map<UUID, TodoDto> tasks;

    public TasksController(Map<UUID, TodoDto> tasks) {
        this.tasks = tasks;
    }

    public Collection<TodoDto> listTasks() {
        return tasks.values();
    }

    public void updateTask(UUID id, UpdateTaskStatusRequestDto update) {
        var task = tasks.get(id);
        if (task == null) {
            throw new IllegalArgumentException("Not found");
        }
        if (!update.missingRequiredFields().isEmpty()) {
            throw new IllegalArgumentException("Missing required fields: " + update.missingRequiredFields());
        }
        task.setStatus(update.getStatus());
    }
}
