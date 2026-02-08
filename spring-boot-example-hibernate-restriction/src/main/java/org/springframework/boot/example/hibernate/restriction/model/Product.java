package org.springframework.boot.example.hibernate.restriction.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Product {

	@Id
	@GeneratedValue
	private Long id;

	private String name;

	private Double price;

	public Product(String name, Double price) {
		this.name = name;
		this.price = price;
	}
}
