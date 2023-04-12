package com.soprasteria.simplejavaee;

import org.junit.jupiter.api.Test;
import simplejavaee.generated.model.SampleModelData;
import simplejavaee.generated.model.TodoItemDto;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;


@TestDataSource
class TodoApiTest {

    private final TodoApi todoApi = new TodoApi();

    private final SampleModelData sampleData = new SampleModelData(System.currentTimeMillis());

    @Test
    void shouldIncludeSavedTodo() throws SQLException {
        var savedTodoItem = sampleData.sampleTodoItemDto();
        todoApi.saveTodo(savedTodoItem);
        assertThat(todoApi.listTodos())
                .extracting(TodoItemDto::getTitle)
                .contains(savedTodoItem.getTitle());
    }


}