package com.nrt.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.nrt.exception.ActiveDeactiveException;
import com.nrt.request.UpdateRequest;
import com.nrt.request.UserRequest;
import com.nrt.service.UserService;
import com.nrt.util.CommonUtil;

/**
 * @author Ramu singh
 */

@Controller
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	CartItemController CartItemController;

	@GetMapping("/user/page")
	public ModelAndView CouponHomePage(ModelAndView modelAndView) {
		modelAndView.setViewName("/html/login/user-register");
		return modelAndView;

	}

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/users-list")
	public ModelAndView getAllUsers(ModelAndView modelAndView) {
		List<UserRequest> userList = userService.getAllUsersList();
		modelAndView.addObject("users", userList);
		modelAndView.setViewName("/html/login/users-list");
		return modelAndView;

	}

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("user-delete")
	public ModelAndView deleteUser(@RequestParam("id") long userId, ModelAndView modelAndView) {
		userService.deleteUser(userId);
		return this.getAllUsers(modelAndView);

	}

	@PostMapping("/user/register")
	public ModelAndView addUser(@ModelAttribute UserRequest userRequest, ModelAndView modelAndView) {
		userService.saveData(userRequest);
		modelAndView.setViewName("redirect:/login");
		return modelAndView;
	}

	@GetMapping("/profile")
	public ModelAndView Profile(ModelAndView modelAndView) {
		UserRequest user = userService.getUserByEmail(CommonUtil.getCurrentUser());
		modelAndView.addObject("response", user);
		modelAndView.addObject("profilePictureUrl", "/images/" + user.getDP());

		modelAndView.setViewName("/html/login/profile");
		return modelAndView;
	}

	@GetMapping("/password/update")
	public ModelAndView UpdatePassword(ModelAndView modelAndView) {
		modelAndView.setViewName("/html/login/password-update");
		return modelAndView;
	}

	@PostMapping("/password/update")
	public ModelAndView PasswordVerify(@ModelAttribute UpdateRequest updateRequest, ModelAndView modelAndView) {
		Boolean flag = userService.updatePassword(updateRequest.getOldPassword(), updateRequest.getNewPassword());
		if (flag) {
			modelAndView.addObject("title", "Successful password updated");
			modelAndView.addObject("message", "Password Updated Successfully..");
			modelAndView.addObject("url", "http://localhost:9090/profile");
			modelAndView.addObject("button", "to Profile");
			modelAndView.addObject("details", "\"Congratulations! Your Password Updated Successfully..");
			modelAndView.setViewName("/html/coupon/response_success");
		} else {
			modelAndView.addObject("title", "Faild to update password ");
			modelAndView.addObject("url", "http://localhost:9090/profile");
			modelAndView.addObject("button", "to Profile");
			modelAndView.addObject("error", "New password matches a previous password.");
			modelAndView.setViewName("/html/coupon/error");
		}
		return modelAndView;
	}

	@GetMapping("/index/page")
	public ModelAndView Index(ModelAndView modelAndView) {
       
		 String userRole = userService.getUserByEmail(CommonUtil.getCurrentUser()).getRequestRole();
		
		if (userRole.equalsIgnoreCase("admin")) {
			modelAndView.setViewName("/html/admin/admin");
			return modelAndView;
		}
			
		if (userRole.equalsIgnoreCase("reseller")) {
			modelAndView.setViewName("/html/shopkeeper/dashboard");
			return modelAndView;
		}
			
		
	   return CartItemController.showAllProductsList(modelAndView);

		

	}

	@GetMapping("/user/updateProfilePage")
	public ModelAndView getUpdateProfilePage(ModelAndView modelAndView) {
		UserRequest userRequest = userService.getUserByEmail(CommonUtil.getCurrentUser());
		modelAndView.addObject("userRequest", userRequest);
		modelAndView.setViewName("/html/login/updateProfile");
		return modelAndView;

	}

	@PostMapping("/user/updateProfile")
	public ModelAndView getUpdateProfile(ModelAndView modelAndView, @ModelAttribute UserRequest userRequest) {
		Boolean flag = userService.updateProfile(userRequest);
		if (flag) {
			modelAndView.addObject("message", "Profile Updated Successfully");
			modelAndView.setViewName("/html/RolePermissions/response-permissions");
		} else {
			modelAndView.addObject("message", "Failed to Edit the profile");
			modelAndView.setViewName("/html/RolePermissions/response-permissions");
		}
		return modelAndView;

	}

	@PostMapping("/save/profile")
	public ModelAndView DP(@RequestParam("file") MultipartFile file, ModelAndView modelAndView) {
		Boolean flag = userService.saveDP(file);
		UserRequest user = userService.getUserByEmail(CommonUtil.getCurrentUser());
		modelAndView.addObject("response", user);
		if (flag) {
			modelAndView.addObject("profilePictureUrl", "/images/" + user.getDP());
			modelAndView.setViewName("/html/login/profile");
		} else {
			modelAndView.addObject("title", "Faild to update Profile ");
			modelAndView.addObject("url", "http://localhost:9090/profile");
			modelAndView.addObject("button", "to Profile");
			modelAndView.addObject("error", "Faild to update Profile");
			modelAndView.setViewName("/html/coupon/error");
		}
		return modelAndView;

	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/user/activeDeactive/")
	public ModelAndView toggleCouponStatus(@RequestParam("userId") long userId,
			@RequestParam("userStatus") int userStatus, ModelAndView modelAndView) {
		try {
			// int getStatus = Integer.parseInt(couponStatus);
			Boolean isUpdated = userService.updateUserStatus(userId, userStatus);
			if (isUpdated) {
				modelAndView.addObject("users", userService.getAllUsersList());
				modelAndView.setViewName("/html/login/users-list");
				return modelAndView;
			} else {
				throw new ActiveDeactiveException("Failed to change the status of user CAUSE:");
			}
		} catch (Exception e) {
			throw new ActiveDeactiveException("Failed to change the status of user CAUSE: " + e.getMessage());
		}
	}

	@PreAuthorize("!hasRole('ADMIN')")
	@GetMapping("/user/deactive")
	public ModelAndView DeactivateAccountHandler(ModelAndView modelAndView) {
		userService.DeactivateAccount();
		modelAndView.setViewName("/html/login/login");
		return modelAndView;

	}

}
