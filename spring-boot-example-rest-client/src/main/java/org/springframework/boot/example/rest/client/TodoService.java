package org.springframework.boot.example.rest.client;

import org.springframework.boot.example.rest.client.model.Todos;
import org.springframework.web.service.annotation.GetExchange;

public interface TodoService {

    @GetExchange("/todos")
    Todos findTodos();
}
