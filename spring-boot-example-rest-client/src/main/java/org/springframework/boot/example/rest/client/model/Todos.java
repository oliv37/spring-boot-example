package org.springframework.boot.example.rest.client.model;

import java.util.List;

public record Todos(List<Todo> todos, Long total, Long skip, Long limit) {
}
