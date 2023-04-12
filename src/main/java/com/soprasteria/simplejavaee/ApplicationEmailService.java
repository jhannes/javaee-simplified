package com.soprasteria.simplejavaee;

import simplejavaee.generated.model.TodoItemDto;

public interface ApplicationEmailService {
    void sendNewTodoEmail(TodoItemDto todoItem);
}
