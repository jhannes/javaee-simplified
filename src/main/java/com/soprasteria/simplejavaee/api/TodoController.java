package com.soprasteria.simplejavaee.api;

import com.soprasteria.generated.simplejavaee.model.TodoDto;
import com.soprasteria.generated.simplejavaee.model.TodoPropertiesDto;
import com.soprasteria.generated.simplejavaee.model.TodoSnapshotDto;
import com.soprasteria.generated.simplejavaee.model.UpdateTodoStateRequestDto;
import com.soprasteria.simplejavaee.ApplicationUserPrincipal;
import com.soprasteria.simplejavaee.TodoDao;
import org.actioncontroller.actions.GET;
import org.actioncontroller.actions.POST;
import org.actioncontroller.actions.PUT;
import org.actioncontroller.optional.json.Json;
import org.actioncontroller.values.PathParam;
import org.actioncontroller.values.UserPrincipal;
import org.fluentjdbc.DbContext;

import java.util.List;
import java.util.UUID;

public class TodoController {
    private final TodoDao dao;
    private final DbContext dbContext;

    public TodoController(DbContext dbContext) {
        this.dao = new TodoDao(dbContext);
        this.dbContext = dbContext;
    }

    @GET("/todos")
    @Json
    public List<TodoSnapshotDto> listTodos() {
        return dao.list();
    }

    @POST("/todos")
    public void newTodo(
            @Json TodoDto newTodo,
            @UserPrincipal ApplicationUserPrincipal principal
    ) {
        dao.save(newTodo, principal);
    }

    @PUT("/todos/{id}")
    public void updateTodo(
            @PathParam("id") UUID id,
            @Json TodoPropertiesDto properties,
            @UserPrincipal ApplicationUserPrincipal principal
    ) {
        dao.update(id, properties, principal);
    }

    @POST("/todos/updateState")
    public void updateTodoState(
            @Json UpdateTodoStateRequestDto updateRequest,
            @UserPrincipal ApplicationUserPrincipal principal
    ) {
        updateAll(updateRequest.getIdList(), new TodoPropertiesDto().state(updateRequest.getState()), principal);
    }

    private void updateAll(List<UUID> idList, TodoPropertiesDto update, ApplicationUserPrincipal principal) {
        try (var dbTransaction = dbContext.ensureTransaction()) {
            for (var id : idList) {
                dao.update(id, update, principal);
            }
            dbTransaction.setComplete();
        }
    }
}
