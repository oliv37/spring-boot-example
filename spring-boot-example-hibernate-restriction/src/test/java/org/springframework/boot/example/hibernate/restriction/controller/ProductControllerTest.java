package org.springframework.boot.example.hibernate.restriction.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.example.hibernate.restriction.model.Product;
import org.springframework.boot.example.hibernate.restriction.repository.ProductRepository;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.util.List;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
class ProductControllerTest {

	List<Product> products;

	RestTestClient client;

	@LocalServerPort
	Integer port;

	@Container
	@ServiceConnection
	static PostgreSQLContainer postgres = new PostgreSQLContainer(
			"postgres:16-alpine"
	);

	@Autowired
	ProductRepository productRepository;

	@BeforeEach
	void setUp() {
		productRepository.deleteAllInBatch();

		products = productRepository.saveAll(List.of(
				new Product("iPhone 15", 999.00),
				new Product("MacBook Pro", 2400.00),
				new Product("Coffee Mug", 15.00)
		));

		client = RestTestClient.bindToServer()
				.baseUrl("")
				.baseUrl("http://localhost:" + port)
				.build();
	}

	@Test
	void testFindById() {
		client.get()
				.uri("/api/products/" + products.getFirst().getId())
				.exchange()
				.expectStatus().isOk()
				.expectBody()
				.jsonPath("$.name").isEqualTo("iPhone 15");
	}
}