package com.nrt.serviceimpl;

import java.util.ArrayList;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
                                                                      
import com.nrt.entity.OrderDetails;
import com.nrt.entity.User;
import com.nrt.repository.OrderDetailsRepo;
import com.nrt.repository.UserRepository;
import com.nrt.util.CommonUtil;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class ShopkeeperService {

	@Autowired
	OrderDetailsRepo orderDetailsRepo;
	@Autowired
	UserRepository userRepository;

	public List<User> getCustomers() {

		List<User> customerList = new ArrayList<User>();
		List<OrderDetails> orderDetails = orderDetailsRepo.findAllByUserId(CommonUtil.getCurrentUser());

		for (OrderDetails orderDetail : orderDetails) {

			customerList.add(userRepository.findByEmail(orderDetail.getUserId()).get());
		}
		log.info("customer list is retrived from orders list");

		return customerList;
	}

	public List<OrderDetails> getOrderHitory(String customerId) {
		log.info("order history called for id :"+customerId);
		return orderDetailsRepo.findAllByUserId(customerId);
	}

}
