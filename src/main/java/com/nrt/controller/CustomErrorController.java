package com.nrt.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Controller
@RequestMapping("/errorController")
public class CustomErrorController implements ErrorController {

	@GetMapping("user_not_found")
	public ModelAndView handleError(@RequestParam("errorMessage") String errorMessage) {
		log.info("user_not_found is called (Exception occurred and redirected to the error page:");
		ModelAndView ModelAndView = new ModelAndView();
		ModelAndView.addObject("error_message", errorMessage);
		ModelAndView.addObject("button", "Try Again");
		ModelAndView.addObject("url", "http://localhost:9090/login");
		ModelAndView.setViewName("/html/coupon/error.html");
		return ModelAndView;
	}

	@GetMapping("deactivated")
	public ModelAndView deactivatedPage(@RequestParam("errorMessage") String errorMessage) {
		log.info("deactivated is called (Exception occurred and redirected to the error page:");
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("message", errorMessage);
		modelAndView.setViewName("/html/RolePermissions/response-permissions");
		return modelAndView;
	}

	@GetMapping("accessDenied")
	public ModelAndView accessDeniedHandler(@RequestParam("errorMessage") String errorMessage) {
		log.info("accessDenied is called (Exception occurred and redirected to the error page:");
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("message", errorMessage);
		modelAndView.setViewName("/html/RolePermissions/response-permissions");
		return modelAndView;
	}

	@GetMapping("page/deactivation")
	public ModelAndView pagedeactivation(ModelAndView modelAndView) {
		log.info("deactivated page is called (Exception occurred and redirected to the error page:");
		modelAndView.setViewName("/html/login/deactivation.html");
		return modelAndView;
	}

	@RequestMapping("/error")
	public String handleError() {
		return "html/coupon/error"; // Return the name of your custom error page
	}

}
