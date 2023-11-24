package com.nrt.util;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.nrt.entity.User;
import com.nrt.repository.UserRepository;

import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
public class CommonUtil {
	@Autowired
	private static UserRepository userRepository;

	public CommonUtil(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public static String changeLongToString(Long id) {

		return String.valueOf(id);
	}

	public static long changeStringToLong(String str) {
		return Long.parseLong(str);
	}

	public static boolean isExpired(LocalDate expiresAtDate) {
		log.info("expiresAtDate :" + expiresAtDate + " LocalDate " + LocalDate.now());
		return LocalDate.now().isBefore(expiresAtDate);
	}

	public static boolean isActive(LocalDate activetAt) {
		log.info("activetAt date :" + activetAt + " LocalDate " + LocalDate.now());
		return LocalDate.now().isAfter(activetAt);
	}

	public static String getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String userEmail = null;
		if (authentication != null) {
			userEmail = authentication.getName();
		}
		return userEmail;
	}

	public static User getCurrentUserDetails() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User userDetails = null;
		if (authentication != null) {
			userDetails = userRepository.findByEmail(authentication.getName()).get();
		}
		return userDetails;
	}

}