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
import java.util.UUID;

public class TasksController {

    private final TasksDao taskDao;

    public TasksController(TasksDao tasks) {
        this.taskDao = tasks;
    }

    @GET("/tasks")
    @JsonBody
    public Collection<TodoDto> listTasks() {
        return taskDao.listAll();
    }


    @POST("/tasks")
    public void createTask(TodoDto task) {
        if (!task.missingRequiredFields().isEmpty()) {
            throw new HttpRequestException("Missing required fields: " + task.missingRequiredFields());
        }
        taskDao.insert(task);
    }

    @PUT("/tasks/{id}")
    public void updateTask(
            @PathParam("id") UUID id,
            @JsonBody UpdateTaskStatusRequestDto update
    ) {
        if (!update.missingRequiredFields().isEmpty()) {
            throw new HttpRequestException("Missing required fields: " + update.missingRequiredFields());
        }
        var task = taskDao.retrieve(id)
                .orElseThrow(() -> new HttpNotFoundException("No task with id=" + id));
        task.setStatus(update.getStatus());
        taskDao.update(task);
    }
}
