package com.nrt.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nrt.entity.OrderDetails;
import com.nrt.entity.OrderedItemDetails;

public interface OrderedItemsDetailsRepo extends JpaRepository<OrderedItemDetails, Long>{

	List<OrderedItemDetails> findByOrder(OrderDetails order);

}
