package org.springframework.boot.example.hibernate.stream.controller;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.stream.IntStream;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

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

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
    }

    @Test
    void testGetEventIds1() {
        given()
                .when()
                .get("/eventIds1")
                .then()
                .statusCode(200)
                .body("$", hasSize(10_000))
                .body("$", equalTo(IntStream.range(1, 10_000 + 1).boxed().toList()));
    }

    @Test
    void testGetEventIds2() {
        given()
                .when()
                .get("/eventIds2")
                .then()
                .statusCode(200)
                .body("$", hasSize(10_000))
                .body("$", equalTo(IntStream.range(1, 10_000 + 1).boxed().toList()));
    }

    @Test
    void testGetEventIds3() {
        given()
                .when()
                .get("/eventIds3")
                .then()
                .statusCode(200)
                .body("$", hasSize(10_000))
                .body("$", equalTo(IntStream.range(1, 10_000 + 1).boxed().toList()));
    }
}
