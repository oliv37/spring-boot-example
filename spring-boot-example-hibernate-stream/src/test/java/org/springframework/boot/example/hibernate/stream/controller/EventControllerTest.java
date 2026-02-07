package org.springframework.boot.example.hibernate.stream.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.client.EntityExchangeResult;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("test")
public class EventControllerTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:16-alpine"
    );

    @LocalServerPort
    Integer port;

    RestTestClient client;

    @BeforeEach
    void setUp() {
        client = RestTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    @Test
    void testGetEventIds1() {
        List<Integer> result = client.get().uri("/eventIds1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<Integer>>() {})
                .returnResult()
                .getResponseBody();

        assertThat(result).isEqualTo(IntStream.range(1, 10_000 + 1).boxed().toList());
    }

    @Test
    void testGetEventIds2() {
        client.get().uri("/eventIds2")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$")
                .isArray()
                .jsonPath("$")
                .isEqualTo(IntStream.range(1, 10_000 + 1).boxed().toList());
    }

    @Test
    void testGetEventIds3() {
        EntityExchangeResult<List<Integer>> result = client.get().uri("/eventIds2")
                .exchange()
                .expectBody(new ParameterizedTypeReference<List<Integer>>() {})
                .returnResult();

        assertThat(result.getStatus()).isEqualTo(HttpStatus.OK);
        assertThat(result.getResponseBody())
                .hasSize(10_000)
                .isEqualTo(IntStream.range(1, 10_000 + 1).boxed().toList());
    }
}
