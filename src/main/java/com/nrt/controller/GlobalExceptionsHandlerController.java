package com.nrt.controller;

import java.nio.file.FileSystemException;

import org.springframework.mail.MailSendException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.xhtmlrenderer.util.XRRuntimeException;

import com.nrt.exception.ActiveDeactiveException;
import com.nrt.exception.CantDeleteException;
import com.nrt.exception.DeactivatedUserException;
import com.nrt.exception.EmailMessagingException;
import com.nrt.exception.FailedToCreateTokenException;
import com.nrt.exception.GeneratePdfException;
import com.nrt.exception.ProductOutOfStockException;
import com.nrt.exception.RoleAssignException;

import lombok.extern.log4j.Log4j2;

@Log4j2
@ControllerAdvice
public class GlobalExceptionsHandlerController {

	@ExceptionHandler(BadCredentialsException.class)
	public ModelAndView handleAuthenticationException(BadCredentialsException e) {
		log.error("BadCredentialsException Exception occurred: " + e.getMessage());
		return renderToResponsePage(e.getMessage());
	}

	@ExceptionHandler(FailedToCreateTokenException.class)
	public ModelAndView getDisablePage(FailedToCreateTokenException e) {
		log.error("FailedToCreateTokenException Exception occurred and redirected to the error page: msg"
				+ e.getMessage());
		ModelAndView modelAndView = new ModelAndView("redirect:/deactivated");
		modelAndView.addObject("errorMessage", "User is deactivated. Please contact support.");
		return modelAndView;
	}

	@ExceptionHandler(DeactivatedUserException.class)
	public ModelAndView handleDeactivatedUserException(DeactivatedUserException e) {
		log.error("DeactivatedUserException Exception occurred and redirected to the error page: " + e.getMessage());
		ModelAndView modelAndView = new ModelAndView("redirect:/deactivated");
		modelAndView.addObject("errorMessage", "User is deactivated. Please contact support.");
		return modelAndView;
	}

	@ExceptionHandler(UsernameNotFoundException.class)
	public ModelAndView handleGlobalException(UsernameNotFoundException e) {
		log.error(" UsernameNotFoundException occurred and redirected to the error page: " + e.getMessage());
		ModelAndView modelAndView = new ModelAndView("redirect:/errorController/user_not_found");
		modelAndView.addObject("errorMessage", e.getMessage());
		return modelAndView;
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ModelAndView handleAuthenticationException(AccessDeniedException e) {
		log.error("AccessDenied Exception occurred: " + e.getMessage());
		ModelAndView modelAndView = new ModelAndView("redirect:/errorController/accessDenied");
		modelAndView.addObject("errorMessage", e.getMessage());
		return modelAndView;
	}

	@ExceptionHandler(EmailMessagingException.class)
	public ModelAndView handleEmailMessagingException(EmailMessagingException e) {
		log.error("EmailMessagingException Exception occurred: and handled " + e.getMessage());
		return renderToResponsePage(e.getMessage());
	}

	@ExceptionHandler(RoleAssignException.class)
	public ModelAndView handleRoleAssignException(RoleAssignException e) {
		log.error("RoleAssignException Exception occurred: and handled " + e.getMessage());
		return renderToResponsePage(e.getMessage());
	}

	@ExceptionHandler(ActiveDeactiveException.class)
	public ModelAndView handleActiveDeactiveException(ActiveDeactiveException e) {
		log.error("ActiveDeactiveException Exception occurred: and handled " + e.getMessage());
		return renderToResponsePage(e.getMessage());
	}

	public static ModelAndView renderToResponsePage(String message) {
		log.info("Rendered on the error page:");
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("message", message);
		modelAndView.setViewName("/html/RolePermissions/response-permissions");
		return modelAndView;
	}

	@ExceptionHandler(CantDeleteException.class)
	public ModelAndView handleCantDeleteException(CantDeleteException e) {
		log.error("CantDeleteException Exception occurred: and handled " + e.getMessage());
		return renderToResponsePage(e.getMessage());
	}

	@ExceptionHandler(FileSystemException.class)
	public void handleFileSystemException(FileSystemException e) {
		log.error("CantDeleteException Exception occurred: and handled " + e.getMessage());
	}
	
	@ExceptionHandler(ProductOutOfStockException.class)
	public ModelAndView handleFileSystemException(ProductOutOfStockException e) {
		log.error("ProductOutOfStockException Exception occurred: and handled " + e.getMessage());
		return renderToResponsePage(e.getMessage());
	}

	@ExceptionHandler(XRRuntimeException.class)
	public void handleXRRuntimeException(XRRuntimeException e) {
		log.error("XRRuntimeException Exception occurred: and handled " + e.getMessage());
		
	}
	
	@ExceptionHandler(GeneratePdfException.class)
	public ModelAndView handleGeneratePdfException(GeneratePdfException e) {
		log.error("GeneratePdfException  occurred: and handled " + e.getMessage());
		return renderToResponsePage(e.getMessage());
	}
	
	@ExceptionHandler(MailSendException.class)
	public ModelAndView handleMailSendExceptionn(MailSendException e) {
		log.error("MailSendException  occurred: and handled " + e.getMessage());
		return renderToResponsePage(e.getMessage());
	}	
	
}
