package com.soprasteria.simplejavaee;

import com.soprasteria.generated.simplejavaee.model.TodoDto;
import com.soprasteria.generated.simplejavaee.model.TodoPropertiesDto;
import com.soprasteria.generated.simplejavaee.model.TodoSnapshotDto;
import com.soprasteria.generated.simplejavaee.model.TodoStateDto;
import org.actioncontroller.exceptions.HttpForbiddenException;
import org.actioncontroller.exceptions.HttpNotFoundException;
import org.fluentjdbc.DbContext;
import org.fluentjdbc.DbContextTable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TodoDao {

    private final DbContextTable table;

    public TodoDao(DbContext dbContext) {
        this.table = dbContext.tableWithTimestamps("todos");
    }

    public void save(TodoDto todo, ApplicationUserPrincipal principal) {
        table.newSaveBuilderWithUUID("id", todo.getId())
                .setField("title", todo.getTitle())
                .setField("description", todo.getDescription())
                .setField("state", todo.getState())
                .setField("created_by", principal.getEmail())
                .setField("updated_by", principal.getEmail())
                .execute();
    }

    public List<TodoSnapshotDto> list() {
        return table.orderedBy("updated_at desc")
                .list(row -> new TodoSnapshotDto()
                .id(row.getUUID("id"))
                .title(row.getString("title"))
                .description(row.getString("description"))
                .state(row.getEnum(TodoStateDto.class, "state"))
                .createdAt(row.getOffsetDateTime("created_at"))
                .createdBy(row.getString("created_by"))
                .updatedAt(row.getOffsetDateTime("updated_at"))
                .updatedBy(row.getString("updated_by"))
        );
    }

    public void update(UUID id, TodoPropertiesDto todo, ApplicationUserPrincipal principal) {
        Optional<String> updatedBy = table.where("id", id).singleString("updated_by");
        if (updatedBy.isEmpty()) {
            throw new HttpNotFoundException("todo with id " + id);
        } else if (!updatedBy.get().equals(principal.getEmail())) {
            throw new HttpForbiddenException("Not owner");
        }

        table.where("id", id).update()
                .setFieldIfPresent("title", todo.getTitle())
                .setFieldIfPresent("state", todo.getState())
                .setFieldIfPresent("description", todo.getDescription())
                .setField("updated_by", principal.getEmail())
                .execute();
    }
}
