package com.nrt.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nrt.entity.OrderDetails;

public interface OrderDetailsRepo extends JpaRepository<OrderDetails, Long> {

	List<OrderDetails> findAllByUserId(String currentUser);

	List<OrderDetails> findAllByShopkeeperId(String shopkeeperId);

}
