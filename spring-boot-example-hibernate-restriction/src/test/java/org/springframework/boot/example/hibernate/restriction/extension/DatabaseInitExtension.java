package org.springframework.boot.example.hibernate.restriction.extension;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.boot.example.hibernate.restriction.model.Product;
import org.springframework.boot.example.hibernate.restriction.repository.ProductRepository;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

public class DatabaseInitExtension implements BeforeEachCallback {

	@Override
	public void beforeEach(ExtensionContext context) {
		// Get the repository from the Spring Context
		ProductRepository productRepository = SpringExtension
				.getApplicationContext(context)
				.getBean(ProductRepository.class);

		productRepository.deleteAllInBatch();
		productRepository.saveAll(List.of(
				new Product("iPhone 15", 999.00),
				new Product("MacBook Pro", 2400.00),
				new Product("Coffee Mug", 15.00)
		));
	}
}