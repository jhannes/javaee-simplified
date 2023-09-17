package com.soprasteria.javaeesimplified;

import com.soprasteria.generated.javaeesimplified.model.TodoDto;
import com.soprasteria.generated.javaeesimplified.model.UpdateTaskStatusRequestDto;
import org.actioncontroller.actions.GET;
import org.actioncontroller.actions.POST;
import org.actioncontroller.actions.PUT;
import org.actioncontroller.exceptions.HttpNotFoundException;
import org.actioncontroller.exceptions.HttpRequestException;
import org.actioncontroller.values.PathParam;
import org.actioncontroller.values.json.JsonBody;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public class TasksController {

    private final Map<UUID, TodoDto> tasks;

    public TasksController(Map<UUID, TodoDto> tasks) {
        this.tasks = tasks;
    }

    @GET("/tasks")
    @JsonBody
    public Collection<TodoDto> listTasks() {
        return tasks.values();
    }


    @POST("/tasks")
    public void createTask(TodoDto task) {
        if (!task.missingRequiredFields().isEmpty()) {
            throw new HttpRequestException("Missing required fields: " + task.missingRequiredFields());
        }
        tasks.put(task.getId(), task);
    }

    @PUT("/tasks/{id}")
    public void updateTask(
            @PathParam("id") UUID id,
            @JsonBody UpdateTaskStatusRequestDto update
    ) {
        var task = tasks.get(id);
        if (task == null) {
            throw new HttpNotFoundException("No task with id=" + id);
        }
        if (!update.missingRequiredFields().isEmpty()) {
            throw new HttpRequestException("Missing required fields: " + update.missingRequiredFields());
        }
        task.setStatus(update.getStatus());
    }
}
