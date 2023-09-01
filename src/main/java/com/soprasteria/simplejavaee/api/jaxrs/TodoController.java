package com.soprasteria.simplejavaee.api.jaxrs;

import com.soprasteria.generated.simplejavaee.model.TodoDto;
import com.soprasteria.simplejavaee.TodoDao;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import org.fluentjdbc.DbContext;

import java.util.List;

@Path("/todos")
public class TodoController {

    private final TodoDao todoDao;

    @Inject
    public TodoController(DbContext dbContext) {
        todoDao = new TodoDao(dbContext);
    }

    @GET
    public List<TodoDto> listTodos() {
        return todoDao.list();
    }

    @POST
    public void createTodo(TodoDto todo) {
        todoDao.save(todo);
    }

}
