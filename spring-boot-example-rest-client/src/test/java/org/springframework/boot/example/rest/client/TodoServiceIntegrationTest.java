package org.springframework.boot.example.rest.client;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.EnabledIf;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EnabledIf(expression = "${integration-tests.enabled}", loadContext = true)
class TodoServiceIntegrationTest {

    @Autowired
    TodoService todoService;

    @Test
    void testFindTodos() {
        assertThat(todoService.findTodos()).isNotNull().satisfies(todos -> {
            assertThat(todos.total()).isGreaterThan(1);
            assertThat(todos.todos()).hasSizeGreaterThan(1);
        });
    }
}
