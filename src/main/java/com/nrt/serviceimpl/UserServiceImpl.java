package com.nrt.serviceimpl;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.nrt.Email.EmailSender;
import com.nrt.authentication.CustomUserDetails;
import com.nrt.authentication.CustomUserService;
import com.nrt.authentication.JwtUtil;
import com.nrt.entity.Role;
import com.nrt.entity.User;
import com.nrt.repository.UserRepository;
import com.nrt.request.LoginRequest;
import com.nrt.request.UserRequest;
import com.nrt.responce.LoginResponce;
import com.nrt.service.UserService;
import com.nrt.util.RandomPasswordGeneratorWithPattern;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private CustomUserService userDetailsService;

	@Autowired
	private JwtUtil jwtUtil;
	@Autowired
	BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private EmailSender emailSender;

	private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

	public ResponseEntity<User> saveData(UserRequest userRequest) {
		Date date = Date.valueOf(new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()));
		User user = new User();
		String hashedPassword = null;
		try {
			user.setEmail(userRequest.getRequestEmialId());
			user.setFirstName(userRequest.getRequestFirstName());
			user.setLastName(userRequest.getRequestLastName());
			user.setPhoneNo(userRequest.getRequestPhone());
			user.setStatus(0);
			user.setCreatedAt(date);
			user.setUpdatedAt(date);
			user.setPasswordUpdated(date);
			user.setRole(new Role("ROLE_" + userRequest.getRequestRole().toUpperCase()));
			String generateRandomPassword = RandomPasswordGeneratorWithPattern.generateRandomPassword();
			hashedPassword = passwordEncoder.encode(generateRandomPassword);
			user.setPassword(hashedPassword);
			user = userRepository.save(user);
			emailSender.sendWelcomeEmail(user.getEmail(),user.getEmail(),
					user.getRole().getRole(), generateRandomPassword,"Registration Successfully Done..!");
		} catch (Exception e) {
			log.info("error inside the user register method");
			log.error(e.getLocalizedMessage());
		}

		return new ResponseEntity<User>(user, HttpStatus.OK);

	}

	@Override
	public ResponseEntity<LoginResponce> generateToken(LoginRequest loginRequest) {
		try {
			this.authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginRequest.getUserId(), loginRequest.getPassword()));
		}

		catch (Exception e) {
			e.printStackTrace();
		}
		String token = null;
		Optional<User> userOptional = java.util.Optional.empty();
		try {
			CustomUserDetails userDetails = this.userDetailsService.loadUserByUsername(loginRequest.getUserId());
			token = jwtUtil.generateToken(userDetails);
			userOptional = userRepository.findByEmail(loginRequest.getUserId());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.ok(new LoginResponce(token, userOptional.get().getRole().getRole()));
	}

	public ResponseEntity<User> getUserById(long id) {

		Optional<User> user = userRepository.findById(id);
		return new ResponseEntity<User>(user.get(), HttpStatus.OK);
	}

	public ResponseEntity<User> updatePassword(String userId, String oldPassword, String newPassword) {

		Optional<User> optionalUser = userRepository.findByEmail(userId);
		if (optionalUser.isPresent()) {
			User user = optionalUser.get();
			String currentPassword = user.getPassword();

			if (passwordEncoder.matches(oldPassword, currentPassword)) {
				user.setPassword(passwordEncoder.encode(newPassword));
				userRepository.save(user);
				return new ResponseEntity<>(user, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(user, HttpStatus.UNAUTHORIZED);
			}
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@Override
	public UserRequest getUserByEmail() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserRequest userRequest = new UserRequest();
		if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			Optional<User> userOption = userRepository.findByEmail(userDetails.getUsername());

			if (userOption.isPresent())
				userRequest.setRequestEmialId(userOption.get().getEmail());
			userRequest.setRequestFirstName(userOption.get().getFirstName());
			userRequest.setRequestLastName(userOption.get().getLastName());
			userRequest.setRequestPhone(userOption.get().getPhoneNo());
			userRequest.setRequestRole(userOption.get().getRole().getRole());
		}
		return userRequest;
	}
}
