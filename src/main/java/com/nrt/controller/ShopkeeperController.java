package com.nrt.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.nrt.entity.OrderDetails;
import com.nrt.entity.User;
import com.nrt.repository.UserRepository;
import com.nrt.serviceimpl.ShopkeeperService;
import com.nrt.util.CommonUtil;

@Controller
public class ShopkeeperController {

	@Autowired
	private ShopkeeperService shopkeeperService;
	@Autowired
	UserRepository userRepository;

	@GetMapping("/customers-list")
	public ModelAndView getCustomersList(ModelAndView modelAndView) {

		modelAndView.addObject("users", shopkeeperService.getCustomers());
		modelAndView.setViewName("/html/shopkeeper/customersList");
		return modelAndView;

	}

	@GetMapping("/orderHistory")
	public ModelAndView getOrderHistory(ModelAndView modelAndView, @RequestParam("Id") String CustomerId) {
		List<OrderDetails> ordersList = shopkeeperService.getOrderHitory(CustomerId);
		User user = userRepository.findByEmail(CommonUtil.getCurrentUser()).get();
		modelAndView.addObject("ordersList", ordersList);
		modelAndView.addObject("user", user);
		modelAndView.setViewName("/html/shopkeeper/orderDetails");
		return modelAndView;

	}

}
