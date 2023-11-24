package com.nrt.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.nrt.entity.CartItem;
import com.nrt.entity.Product;
import com.nrt.service.CartItemService;
import com.nrt.service.ProductService;
import com.nrt.util.CommonUtil;

import lombok.extern.log4j.Log4j2;

@Controller
@Log4j2
public class CartItemController {

	@Autowired
	CartItemService CartItemService;

	@Autowired
	ProductService productService;

	@PostMapping("/add-to-cart/")
	public ModelAndView addProductToCart(@RequestParam("productId") long id, @RequestParam("quantity") int quantity,
			ModelAndView modelAndView) {
		CommonUtil.getCurrentUser();
		Boolean isSaved = CartItemService.addItemTocart(id, quantity, CommonUtil.getCurrentUser());
		if (isSaved) {
			return showAllProductsList(modelAndView);
		} else {
			modelAndView.addObject("message", "Failed to add this product to cart");
			modelAndView.setViewName("/html/RolePermissions/response-permissions");
			return modelAndView;
		}

	}

	@GetMapping("/purchase-product")
	public ModelAndView showAllProductsList(ModelAndView modelAndView) {
		log.info("purchase pruduct is called");
		String userEmail = CommonUtil.getCurrentUser();
		List<Product> products = productService.getAllProduct();
		modelAndView.addObject("products", products);
		modelAndView.addObject("userId", userEmail);
		modelAndView.setViewName("index");
		return modelAndView;
	}

	@GetMapping("/delete-cartItem/")
	public ModelAndView deleteCart(@RequestParam("id") long cartId, ModelAndView modelAndView) {
		CartItemService.deleteByCartId(cartId);
		return showcart(modelAndView);

	}

	@GetMapping("/view-cart/")
	public ModelAndView showcart(ModelAndView modelAndView) {
		String userId = CommonUtil.getCurrentUser();
		List<CartItem> cartItems = CartItemService.getUsersAllCartItems(userId);
		long cartTotal = 0;
		if (cartItems != null)
			for (CartItem cartItem : cartItems) {
				cartTotal += cartItem.getQuantity() * cartItem.getProduct().getSellingPrice();
			}
		modelAndView.addObject("cartTotal", cartTotal);
		modelAndView.addObject("cartItem", cartItems);
		modelAndView.addObject("userId", userId);
		modelAndView.setViewName("/html/cart/cartView");
		return modelAndView;
	}

//	@GetMapping("/view-all-cart")
//	public ModelAndView showcart(ModelAndView modelAndView) {
//		List<CartItem> cartItems = CartItemService.getAllCartItems();
//		if (cartItems.isEmpty()) {
//			modelAndView.addObject("message", " cart si EMPTY  (please add product to cart)");
//			modelAndView.setViewName("/html/RolePermissions/response-permissions");
//			return modelAndView;
//		}
//		long cartTotal = 0;
//		for (CartItem cartItem : cartItems) {
//			cartTotal += cartItem.getQuantity() * cartItem.getProduct().getSellingPrice();
//		}
//		modelAndView.addObject("cartTotal", cartTotal);
//		modelAndView.addObject("cartItem", cartItems);
//		modelAndView.setViewName("/html/cart/cartView");
//		return modelAndView;
//	}

}
