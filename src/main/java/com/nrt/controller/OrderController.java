package com.nrt.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.nrt.entity.OrderDetails;
import com.nrt.entity.OrderedItemDetails;
import com.nrt.entity.User;
import com.nrt.repository.OrderDetailsRepo;
import com.nrt.repository.ProductRepository;
import com.nrt.repository.UserRepository;
import com.nrt.service.ProductService;
import com.nrt.serviceimpl.CartItemServiceImpl;
import com.nrt.serviceimpl.OrderService;
import com.nrt.serviceimpl.UserServiceImpl;
import com.nrt.util.CommonUtil;

import lombok.extern.log4j.Log4j2;

@Controller
@RequestMapping("order")
@Log4j2
public class OrderController {

	@Autowired
	OrderService orderService;
	@Autowired
	UserServiceImpl userServiceImpl;
	@Autowired
	CartItemServiceImpl cartItemServiceImpl;
	@Autowired
	ProductRepository productRepository;
	@Autowired
	OrderDetailsRepo orderDetailsRepo;
	@Autowired
	ProductService productService;
	@Autowired
	UserRepository userRepository;

	@PostMapping("/summary/")
	public ModelAndView gerOrderSummary(ModelAndView modelAndView, @RequestParam("code") String code,
			@RequestParam("discountedPrice") String discountedPrice, @RequestParam("totalAmount") String totalAmount) {
		return orderService.getOrderSummary(modelAndView, code, discountedPrice, totalAmount);
	}

	@GetMapping("/paymentOptions")
	public ModelAndView getPaymentOptions(ModelAndView modelAndView, @RequestParam("totalAmount") String totalAmount) {
		modelAndView.addObject("totalAmount", totalAmount);
		modelAndView.setViewName("/html/buyOrder/paymentOptions");
		return modelAndView;
	}

	@GetMapping("/buySingleItem")
	public ModelAndView buySingleItem(ModelAndView modelAndView, @RequestParam("quantity") String quantity,
			@RequestParam("productId") String productId) {

		return orderService.getSingleItemOrderSummary(modelAndView, quantity, productId);

	}

	@GetMapping("/getPaymentPage")
	public ModelAndView getPaymentPage(@RequestParam("totalAmount") String amount,
			@RequestParam("paymentMode") String paymentMode, ModelAndView modelAndView) {
		log.info("totalAmount is :" + amount);
		log.info("paymentMode is :" + paymentMode);

		if (paymentMode.equalsIgnoreCase("cashOnDelivery")) {

			String UserId = CommonUtil.getCurrentUser();
			modelAndView.addObject("cartItems", cartItemServiceImpl.getUsersAllCartItems(UserId));
			modelAndView.addObject("payableAmount", amount);
			modelAndView.addObject("user", userServiceImpl.getUserByEmail(UserId));
			modelAndView.setViewName("/html/buyOrder/orderPlaced");
			return modelAndView;
		}

		modelAndView.addObject("totalAmount", amount);
		modelAndView.setViewName("/html/buyOrder/payment");
		return modelAndView;

	}

	@GetMapping("/orders-list")
	public ModelAndView ordersList(ModelAndView modelAndView) {
		log.info("ordersList is called :");
		List<OrderDetails> ordersList = orderService.getAllOrderDetails();
		modelAndView.addObject("ordersList", ordersList);
		modelAndView.addObject("user", userRepository.findByEmail(CommonUtil.getCurrentUser()).get());
		modelAndView.setViewName("/html/shopkeeper/orderDetails");
		return modelAndView;
	}

	@GetMapping("/one-order-details/")
	public ModelAndView OneOrderDetails(ModelAndView modelAndView, @RequestParam("orderId") String orderId) {
		User user = userRepository.findByEmail(CommonUtil.getCurrentUser()).get();
		Long id = Long.parseLong(orderId);
		log.info("single order details  is called :" + orderId);
		OrderDetails ordersDetail = orderDetailsRepo.findById(id).get();
		List<OrderedItemDetails> orderedItemList = orderService.getOneOrderDetails(ordersDetail);
		modelAndView.addObject("order", ordersDetail);
		modelAndView.addObject("productList", orderedItemList);
		modelAndView.addObject("user", user);
		modelAndView.setViewName("/html/shopkeeper/oneOrderDetail");
		return modelAndView;
	}

	@GetMapping("/my-order")
	public ModelAndView myOrders(ModelAndView modelAndView) {
		log.info("myOrders is called:");

		User user = userRepository.findByEmail(CommonUtil.getCurrentUser()).get();
		List<OrderDetails> ordersList = orderService.getAllUserOrderDetails();
		modelAndView.addObject("ordersList", ordersList);
		modelAndView.addObject("user", user);
		modelAndView.setViewName("/html/shopkeeper/orderDetails");
		return modelAndView;
	}

	@GetMapping("/acceptOrders")
	public ModelAndView AcceptOrders(ModelAndView modelAndView, @RequestParam("orderId") String OrderId) {

		Long orderId = Long.parseLong(OrderId);
		productService.decreaseProductQuantityCount(orderId);
		log.info("accept order is called:" + OrderId);

		log.info("Order iD IS id " + OrderId);
		User user = userRepository.findByEmail(CommonUtil.getCurrentUser()).get();
		if (user.getRole().getName().equalsIgnoreCase("Reseller") || user.getRole().getName().equalsIgnoreCase("Admin"))
			return this.ordersList(modelAndView);

		List<OrderDetails> ordersList = orderService.getAllUserOrderDetails();
		modelAndView.addObject("ordersList", ordersList);
		modelAndView.addObject("user", user);
		modelAndView.setViewName("/html/shopkeeper/orderDetails");
		return modelAndView;
	}

	@GetMapping("/cancelOrder")
	public ModelAndView cancelOrders(ModelAndView modelAndView, @RequestParam("orderId") String OrderId) {
		productService.OrderCancelled(Long.parseLong(OrderId));
		log.info("cancel order method is called:" + OrderId);

		User user = userRepository.findByEmail(CommonUtil.getCurrentUser()).get();
		if (user.getRole().getName().equalsIgnoreCase("Reseller") || user.getRole().getName().equalsIgnoreCase("Admin"))
			return this.ordersList(modelAndView);

		List<OrderDetails> ordersList = orderService.getAllUserOrderDetails();
		modelAndView.addObject("ordersList", ordersList);
		modelAndView.addObject("user", user);
		modelAndView.setViewName("/html/shopkeeper/orderDetails");
		return modelAndView;
	}

}
