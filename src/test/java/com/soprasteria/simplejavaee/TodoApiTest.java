package com.soprasteria.simplejavaee;

import simplejavaee.generated.model.SampleModelData;
import simplejavaee.generated.model.TodoItemDto;

import static org.junit.jupiter.api.Assertions.*;

class TodoApiTest {

    private final TodoApi todoApi = new TodoApi();

    private final SampleModelData sampleData = new SampleModelData(-1);

    @Test
    void shouldIncludeSavedTodo() {
        var savedTodoItem = sampleData.sampleTodoItemDto();
        todoApi.saveTodo(savedTodoItem);
        assertThat(todoApi.listTodos())
                .extracting(TodoItemDto::getTitle)
                .contains(savedTodoItem.getTitle());
    }


}