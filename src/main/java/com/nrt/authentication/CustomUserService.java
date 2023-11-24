package com.nrt.authentication;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import com.nrt.entity.User;
import com.nrt.exception.DeactivatedUserException;
import com.nrt.repository.UserRepository;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class CustomUserService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private com.nrt.serviceimpl.PermissionService PermissionService;

	@Override
	public CustomUserDetails loadUserByUsername(String email) throws DeactivatedUserException{

		Optional<User> userOptional = userRepository.findByEmail(email);
		if (userOptional.isPresent()) {
			log.debug("user is present");
			if (userOptional.get().getStatus() == 1) {
				log.debug("status of user is :" + userOptional.get().getStatus());
				return new CustomUserDetails(userOptional.get(), PermissionService);
			} else {
				log.error("DeactivatedUserException is thrown");
				throw new DeactivatedUserException("User is Disabled please reactivate this ");
			}

		} else {
			log.error("BadCredentialsException is thrown");
			throw new BadCredentialsException("Bad credentials, User not found with email: " + email);
		}
			
	}

}