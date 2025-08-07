package org.springframework.boot.example.rest.client.model;

public record Todo(Long id, String todo, Boolean completed, String userId) {
}
