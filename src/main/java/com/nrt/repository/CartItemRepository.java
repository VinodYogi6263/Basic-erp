package com.nrt.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nrt.entity.CartItem;
import com.nrt.entity.Product;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

	List<CartItem> findByUserId(String userId);

	CartItem findByProductId(long productId);
	
	CartItem findByProductIdAndUserId(long productId, String userId);

	void deleteAllByUserId(String currentUser);
	
	List<CartItem> findAllByProduct(Product product);
	
}
