package com.nrt.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.nrt.entity.CartItem;
import com.nrt.request.CouponRequest;
import com.nrt.responce.ApplyCouponResponse;
import com.nrt.service.CartItemService;
import com.nrt.service.CouponService;
import com.nrt.util.CommonUtil;

import jakarta.servlet.http.HttpSession;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Controller
@RequestMapping("/coupon")
public class CouponController {

	@Autowired
	private CouponService couponService;
	@Autowired
	CartItemService cartItemService;
	@Autowired
	ApplyCouponResponse applyCouponResponse;

	// this methos redirect coupon register page
	@GetMapping("/register/page")
	@PreAuthorize("hasRole('ADMIN') or hasRole('COUPON-ADD')")
	public ModelAndView CouponHomePage(ModelAndView modelAndView) {
		modelAndView.setViewName("/html/coupon/coupon_register");
		return modelAndView;

	}

	// this method insert coupon in data base
	@PostMapping("/register")
	@PreAuthorize("hasRole('ADMIN') or hasRole('COUPON-ADD')")
	public ModelAndView Register(@ModelAttribute("couponRequest") CouponRequest couponRequest,
			ModelAndView modelAndView) {
		modelAndView.addObject("title", "Successful register");
		modelAndView.addObject("message", "Coupon registered successfully!");
		modelAndView.addObject("url", "http://localhost:9090/coupon/list");
		modelAndView.addObject("button", "to Home");
		modelAndView.addObject("details", "\"Congratulations! Your coupon registration was successful.!");
		modelAndView.addObject("error", "An error occurred while processing your request. Please try again later.");
		modelAndView.setViewName(
				couponService.Register(couponRequest) ? "/html/coupon/response_success" : "/html/coupon/error");
		return modelAndView;

	}

	// this method find the data by coupon id
	@GetMapping("/edit-coupon/{couponId}")
	@PreAuthorize("hasRole('ADMIN') or hasRole('COUPON-UPDATE')")
	public ModelAndView editCoupon(@PathVariable("couponId") String couponId, @RequestParam("flag") String flag,
			ModelAndView modelAndView) {
		CouponRequest couponRequest = null;
		if (flag.equals("DELETE")) {
			modelAndView.addObject("message", "Coupon Deleted successfully!");
			modelAndView.addObject("details", "Congratulations! Your coupon delete was successful.!");
			modelAndView.setViewName(
					couponService.Delete(CommonUtil.changeStringToLong(couponId)) ? "/html/coupon/response_success"
							: "/html/coupon/error");
		} else {
			couponRequest = couponService.getCouponById(CommonUtil.changeStringToLong(couponId));
			modelAndView.setViewName(couponRequest != null ? "/html/coupon/coupon_update" : "/html/coupon/error");
		}
		modelAndView.addObject("url", "http://localhost:9090/coupon/list");
		modelAndView.addObject("button", "to Home");
		modelAndView.addObject("coupon", couponRequest);
		modelAndView.addObject("error", "Please Inter vailid id . And  try again.");
		return modelAndView;
	}

	// this method update coupon in data base
	@PostMapping("/update")
	@PreAuthorize("hasRole('ADMIN') or hasRole('COUPON-UPDATE')")
	public ModelAndView Update(@ModelAttribute("couponRequest") CouponRequest couponRequest,
			ModelAndView modelAndView) {
		modelAndView.addObject("title", "Successful updated");
		modelAndView.addObject("url", "http://localhost:9090/coupon/list");
		modelAndView.addObject("button", "to Home");
		modelAndView.addObject("message", "Coupon updated successfully!");
		modelAndView.addObject("details", "\"Congratulations! Your coupon updation was successful.!");
		modelAndView.addObject("error", "An error occurred while processing your request. Please try again later.");
		modelAndView.setViewName(
				couponService.Register(couponRequest) ? "/html/coupon/response_success" : "/html/coupon/error");
		return modelAndView;

	}

	// this method provid list of coupon
	@GetMapping("/list")
	@PreAuthorize("hasRole('ADMIN') or hasRole('COUPON-LIST')")
	public ModelAndView CouponList(ModelAndView modelAndView) {
		modelAndView.addObject("listCoupon", couponService.FindAllCoupon());
		modelAndView.setViewName("/html/coupon/List_of_coupon.html");
		return modelAndView;

	}

	@PostMapping("/apply-coupon/")
	public ModelAndView applyCoupon(@RequestParam("code") String couponCode, ModelAndView modelAndView,
			HttpSession session) {
		String userId = CommonUtil.getCurrentUser();
		applyCouponResponse = couponService.applyCoupon(couponCode, userId, session);
		if (applyCouponResponse.getIsApplied()) {

			couponService.addCouponCount(couponCode, userId);

			long discountedPrice = applyCouponResponse.getCartTotalAfterCouponApplied();
			log.info("cart total after coupon applied :" + discountedPrice);
			modelAndView.addObject("cartTotal1", discountedPrice);

			List<CartItem> cartItems = cartItemService.getUsersAllCartItems(userId);
			long cartTotal = 0;
			for (CartItem cartItem : cartItems) {
				cartTotal += cartItem.getQuantity() * cartItem.getProduct().getSellingPrice();
			}
			long saved = cartTotal - discountedPrice;
			modelAndView.addObject("userId", CommonUtil.getCurrentUser());
			modelAndView.addObject("code", couponCode);
			modelAndView.addObject("discountedPrice", discountedPrice);
			modelAndView.addObject("saved", saved);
			modelAndView.addObject("cartTotal", cartTotal);
			modelAndView.addObject("cartItem", cartItems);
			modelAndView.addObject("saved_level_name", "You saved : ₹");
			modelAndView.addObject("cart_total_level_name", "Payable Amount :₹");
			modelAndView.addObject("afterc_oupon_message", "Coupon applyied ");
			modelAndView.setViewName("/html/cart/cartView");
			return modelAndView;
		} else {
			String message = applyCouponResponse.getMessage();
			modelAndView.addObject("message", message);
			modelAndView.setViewName("/html/RolePermissions/response-permissions");
			return modelAndView;
		}

	}

	@GetMapping("/activeDeactive/")
	public ModelAndView toggleCouponStatus(@RequestParam("couponId") long couponId,
			@RequestParam("couponStatus") int couponStatus, ModelAndView modelAndView) {
		log.info("url worked: " + couponId + "  status:" + couponStatus);
		try {
			// int getStatus = Integer.parseInt(couponStatus);
			Boolean isUpdated = couponService.updateCouponStatus(couponId, couponStatus);
			if (isUpdated) {
				modelAndView.addObject("listCoupon", couponService.FindAllCoupon());
				modelAndView.setViewName("/html/coupon/List_of_coupon.html");
				return modelAndView;
			} else {
				modelAndView.addObject("message", " Failed to update this Status");
				modelAndView.setViewName("/html/RolePermissions/response-permissions");
				return modelAndView;
			}
		} catch (

		Exception e) {
			modelAndView.addObject("message", "Error Occoured: ".concat(e.getMessage()));
			modelAndView.setViewName("/html/RolePermissions/response-permissions");
			return modelAndView;
		}
	}

}