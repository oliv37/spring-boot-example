package org.springframework.boot.example.hibernate.restriction.controller;

import lombok.AllArgsConstructor;
import org.hibernate.query.restriction.Restriction;
import org.springframework.boot.example.hibernate.restriction.model.Product;
import org.springframework.boot.example.hibernate.restriction.model.Product_;
import org.springframework.boot.example.hibernate.restriction.repository.ProductRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
@AllArgsConstructor
public class ProductController {

	private final ProductRepository productRepository;

	@GetMapping("/{productId}")
	public Product findById(@PathVariable Long productId) {
		Specification<Product> spec1 = (root, query, cb) ->
				cb.equal(root.get(Product_.id), productId);

		Specification<Product> spec2 = (root, query, cb) ->
			Restriction.equal(Product_.id, productId).toPredicate(root, cb);

		Specification<Product> spec3 = spec2.and((root, query, cb) -> {
			return Restriction.greaterThanOrEqual(Product_.price, 10.0)
					.toPredicate(root, cb);
		});

		return productRepository.findAll(spec2)
				.stream()
				.findFirst()
				.orElse(null);
	}
}
