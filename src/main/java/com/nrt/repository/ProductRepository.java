package com.nrt.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nrt.entity.Product;


public interface ProductRepository extends JpaRepository<Product, Long> {
	public  Product findByName(String name);

	public List<Product> findAllByShopkeeperId(String shopkeeper);
}
