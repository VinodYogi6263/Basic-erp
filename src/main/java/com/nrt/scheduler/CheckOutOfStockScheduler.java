package com.nrt.scheduler;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nrt.entity.CartItem;
import com.nrt.repository.CartItemRepository;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class CheckOutOfStockScheduler implements Runnable {

	@Autowired
	CartItemRepository cartItemRepository;

	@Override
	public void run() {

		log.info("checkOutOfStockScheduler is executed successfully.. ");
		List<CartItem> cartItems = cartItemRepository.findAll();
		
		for(CartItem CartItem :cartItems ) {
			
			if(CartItem.getProduct().getQuantity()==0)
			{
				
			}
		}

	}

}
