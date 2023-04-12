package com.soprasteria.simplejavaee;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import simplejavaee.generated.model.SampleModelData;
import simplejavaee.generated.model.TodoItemDto;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@TestDataSource
class TodoApiTest {
    private ApplicationEmailService emailServiceMock = mock(ApplicationEmailService.class);

    private final TodoApi todoApi = new TodoApi(emailServiceMock);

    private final SampleModelData sampleData = new SampleModelData(System.currentTimeMillis());

    @Test
    void shouldIncludeSavedTodo() throws SQLException {
        var savedTodoItem = sampleData.sampleTodoItemDto();
        todoApi.saveTodo(savedTodoItem);
        assertThat(todoApi.listTodos())
                .extracting(TodoItemDto::getTitle)
                .contains(savedTodoItem.getTitle());
    }

    @Test
    void shouldSendNewEmailOnSavedTodo() throws SQLException {
        var savedTodoItem = sampleData.sampleTodoItemDto();
        todoApi.saveTodoAndEmail(savedTodoItem);
        verify(emailServiceMock).sendNewTodoEmail(savedTodoItem);
    }

    @Test
    void shouldRollbackInsertWhenEmailFails() throws SQLException {
        doThrow(new RuntimeException("Email sending failed")).when(emailServiceMock).sendNewTodoEmail(any());

        var savedTodoItem = sampleData.sampleTodoItemDto();
        assertThatThrownBy(() -> todoApi.saveTodoAndEmail(savedTodoItem))
                .hasMessage("Email sending failed");

        assertThat(todoApi.listTodos())
                .extracting(TodoItemDto::getId)
                .doesNotContain(savedTodoItem.getId());
    }


}