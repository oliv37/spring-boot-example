package org.springframework.boot.example.rest.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.example.rest.client.model.Todo;
import org.springframework.boot.example.rest.client.model.Todos;
import org.springframework.boot.test.context.SpringBootTest;
import org.wiremock.spring.EnableWireMock;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@SpringBootTest(properties = {
        "todo.base-url=${wiremock.server.baseUrl}"
})
@EnableWireMock
class TodoServiceWireMockTest {

    @Autowired
    ObjectMapper mapper;

    @Autowired
    TodoService todoService;

    @Test
    void testFindTodos() throws JsonProcessingException {
        Todos todos = new Todos(List.of(new Todo(1L, "toto", false, "2")), 1L, 0L, 30L);

        stubFor(get("/todos")
                .willReturn(okForContentType(APPLICATION_JSON_VALUE, mapper.writeValueAsString(todos))));

        assertThat(todoService.findTodos()).isNotNull().satisfies(it -> {
            assertThat(it.total()).isEqualTo(1);
            assertThat(it.todos()).hasSize(1);
        });
    }
}
