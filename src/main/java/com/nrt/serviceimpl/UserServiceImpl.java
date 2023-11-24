package com.nrt.serviceimpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.nrt.Email.EmailSender;
import com.nrt.authentication.CustomUserDetails;
import com.nrt.authentication.CustomUserService;
import com.nrt.authentication.JwtUtil;
import com.nrt.entity.Role;
import com.nrt.entity.User;
import com.nrt.exception.ActiveDeactiveException;
import com.nrt.exception.CantDeleteException;
import com.nrt.exception.DeactivatedUserException;
import com.nrt.exception.EmailMessagingException;
import com.nrt.exception.RoleAssignException;
import com.nrt.repository.PaymentDetailsRepo;
import com.nrt.repository.RoleRepository;
import com.nrt.repository.UserCouponMappingRepo;
import com.nrt.repository.UserRepository;
import com.nrt.request.LoginRequest;
import com.nrt.request.UserRequest;
import com.nrt.responce.LoginResponce;
import com.nrt.service.UserService;
import com.nrt.util.CommonUtil;
import com.nrt.util.OTPGenerator;
import com.nrt.util.RandomPasswordGeneratorWithPattern;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	private CustomUserService userDetailsService;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private PasswordService passwordService;

	@Autowired
	PaymentDetailsRepo paymentDetailsRepo;

	@Autowired
	UserCouponMappingRepo userCouponMappingRepo;

	@Autowired
	EmailSender emailSender;

	private static final String OTP_COOKIE_NAME = "OTP";

	@Value("${image.upload.path}")
	private String imageUploadPath;

	private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

	public ResponseEntity<User> saveData(UserRequest userRequest) {
		Date date = Date.valueOf(new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()));
		User user = new User();
		user.setEmail(userRequest.getRequestEmailId());
		user.setFirstName(userRequest.getRequestFirstName());
		user.setLastName(userRequest.getRequestLastName());
		user.setPhoneNo(userRequest.getRequestPhone());
		user.setAddress(userRequest.getRequestAddress());
		user.setStatus(1);
		user.setCreatedAt(date);
		user.setUpdatedAt(date);
		String generateRandomPassword = RandomPasswordGeneratorWithPattern.generateRandomPassword();
		String hashedPassword = passwordEncoder.encode(generateRandomPassword);
		user.setPassword(hashedPassword);
		user = userRepository.save(user);
		this.assignDefaultRoleToUser(user.getEmail());
		Map<String, Object> sourceMap = new HashMap<String, Object>();
		sourceMap.put("password", generateRandomPassword);
		sourceMap.put("username", user.getFirstName() + " " + user.getLastName());
		sourceMap.put("usermail", user.getEmail());
		try {
			emailSender.sendEmail(user.getEmail(), "Registration Successfully Done..!", "/html/email/welcome-template",
					sourceMap);
		} catch (MessagingException e) {
			throw new EmailMessagingException(
					"Failed to Send the email" + user.getEmail() + " Messsage " + e.getMessage());
		}
		return new ResponseEntity<User>(user, HttpStatus.OK);

	}

	@Override
	public ResponseEntity<LoginResponce> generateToken(LoginRequest loginRequest) {
		String token = null;
		Optional<User> userOptional = java.util.Optional.empty();
		try {
			this.authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginRequest.getUserId(), loginRequest.getPassword()));
			log.info("succesfull to create token ....");

			CustomUserDetails userDetails = this.userDetailsService.loadUserByUsername(loginRequest.getUserId());
			token = jwtUtil.generateToken(userDetails);
			userOptional = userRepository.findByEmail(loginRequest.getUserId());
			log.info("user found ....");
			return ResponseEntity.ok(new LoginResponce(token, userOptional.get().getPasswordUpdated()));
		} catch (Exception e) {
			log.error("exception thrown DeactivatedUserException handled ");
			if (e instanceof DeactivatedUserException) {
				log.error("DeactivatedUserException thrown DeactivatedUserException handled ");
				return ResponseEntity.status(444).body(null);
			}

		}
		return ResponseEntity.ok(new LoginResponce(token, null));
	}

	public ResponseEntity<User> getUserById(long id) {
		Optional<User> user = userRepository.findById(id);
		return new ResponseEntity<User>(user.get(), HttpStatus.OK);
	}

	public Boolean updatePassword(String oldPassword, String newPassword) {
		Optional<User> optionalUser = null;
		UserDetails userDetails = null;
		Boolean flag = Boolean.FALSE;
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
			userDetails = (UserDetails) authentication.getPrincipal();
			optionalUser = userRepository.findByEmail(userDetails.getUsername());
		}
		if (optionalUser.isPresent()) {
			User user = optionalUser.get();
			String currentPassword = user.getPassword();

			if (passwordEncoder.matches(oldPassword, currentPassword)) {
				try {
					flag = passwordService.changePassword(userDetails.getUsername(), newPassword);

				} catch (Exception e) {
					e.printStackTrace();
				}

				try {
					Map<String, Object> sourceMap = new HashMap<String, Object>();
					sourceMap.put("username", user.getFirstName() + " " + user.getLastName());
					emailSender.sendEmail(user.getEmail(), "Password Updated Successfully",
							"/html/email/update-password-template", sourceMap);
				} catch (MessagingException e) {
					throw new EmailMessagingException("Failed to send the Email with errorMsg>" + e.getMessage());
				}

				return flag;

			} else {
				return flag;
			}
		} else {
			return flag;
		}

	}

	@Override
	public UserRequest getUserByEmail(String userEmail) {
		UserRequest userRequest = new UserRequest();
		Optional<User> userOption = userRepository.findByEmail(userEmail);

		if (userOption.isPresent()) {
			userRequest.setRequestEmailId(userOption.get().getEmail());
			userRequest.setRequestFirstName(userOption.get().getFirstName());
			userRequest.setRequestLastName(userOption.get().getLastName());
			userRequest.setRequestPhone(userOption.get().getPhoneNo());
			userRequest.setDP(userOption.get().getDp());
			userRequest.setRequestRole(userOption.get().getRole().getName());
			userRequest.setRequestAddress(userOption.get().getAddress());
		}
		return userRequest;
	}

	@Override
	public Boolean SendOTP(String email, HttpServletResponse response) {
		Optional<User> userOption = userRepository.findByEmail(email);
		if (userOption.isPresent()) {
			Map<String, Object> sourceMap = new HashMap<String, Object>();
			String generateOTP = OTPGenerator.generateOTP(6);
			sourceMap.put("username", userOption.get().getFirstName() + " " + userOption.get().getLastName());
			Cookie otpCookie = new Cookie("OTP", generateOTP);
			otpCookie.setMaxAge(24 * 60 * 60);
			otpCookie.setPath("/");
			response.addCookie(otpCookie);
			Cookie emailCookie = new Cookie("email", email);
			emailCookie.setMaxAge(24 * 60 * 60);
			emailCookie.setPath("/");
			response.addCookie(emailCookie);
			sourceMap.put("OTP", generateOTP);
			try {
				emailSender.sendEmail(email, "FORGOT PASSWORD ", "/html/email/forgot-password-template", sourceMap);
				return Boolean.TRUE;
			} catch (MessagingException e) {
				throw new EmailMessagingException("Failed to send the Email with errorMsg>" + e.getMessage());
			}
		}
		return Boolean.FALSE;

	}

	@Override
	public Boolean OTPVelidation(String otp, HttpServletRequest request) {
		String OTP = null;
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(OTP_COOKIE_NAME)) {
					OTP = cookie.getValue();
					if (otp.equals(OTP))

						return Boolean.TRUE;

					else
						return Boolean.FALSE;
				}
			}

		}
		return Boolean.FALSE;

	}

	@Override
	public Boolean ForgotPassword(String newPassword, HttpServletRequest request, HttpServletResponse response) {
		Date date = Date.valueOf(new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()));
		Cookie[] cookies = request.getCookies();
		Boolean flag = Boolean.FALSE;
		String email = null;
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("email")) {
					email = cookie.getValue();
					break;
				}
			}
		}
		if (email != null) {
			Cookie tokenCookie = new Cookie("email", "");
			tokenCookie.setMaxAge(0);
			tokenCookie.setPath("/");
			response.addCookie(tokenCookie);
		}
		Optional<User> userOption = userRepository.findByEmail(email);

		if (userOption.isPresent()) {
			User user = userOption.get();
			user.setPasswordUpdated(date);

			flag = passwordService.changePassword(user.getEmail(), newPassword);

		}
		return flag;
	}

	@Override
	public Boolean saveDP(MultipartFile file) {
		String imagePath = imageUploadPath + file.getOriginalFilename();
		Path destination = Paths.get(imagePath);
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			Optional<User> userOption = userRepository.findByEmail(userDetails.getUsername());

			if (userOption.isPresent()) {
				userOption.get().setDp(file.getOriginalFilename());
				userRepository.save(userOption.get());
			} else {
				return Boolean.FALSE;
			}
		}

		try {
			Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
			return Boolean.TRUE;
		} catch (IOException e) {
			log.error("failed to change  the dp ");
			e.printStackTrace();
		}
		return Boolean.FALSE;
	}

	@Override
	public void assignDefaultRoleToUser(String email) {

		Role role;
		try {
			User user = userRepository.findByEmail(email).orElse(null);
			role = roleRepository.findByName("guest");
			user.setRole(role);
			roleRepository.save(role);
			userRepository.save(user);
			log.info("Default Role assign successsfully as Guest to the user ");
		} catch (Exception e) {
			log.error("failed to assign the role to user exception");
			throw new RoleAssignException("Failed to assign the default role Cause :" + e.getMessage());
		}

	}

	@Override
	public List<UserRequest> getAllUsersList() {
		List<User> userList = userRepository.findAll();
		List<UserRequest> requestUserList = new ArrayList<UserRequest>();
		if (!userList.isEmpty()) {

			for (User user : userList) {
				UserRequest userRequest = new UserRequest();
				userRequest.setRequestId(user.getId());
				userRequest.setRequestEmailId(user.getEmail());
				userRequest.setRequestFirstName(user.getFirstName());
				userRequest.setRequestLastName(user.getLastName());
				userRequest.setRequestPhone(user.getPhoneNo());
				userRequest.setRequestRole(user.getRole().getName());
				userRequest.setRequestStatus(user.getStatus());
				requestUserList.add(userRequest);
			}
		}
		return requestUserList;
	}

	@Override
	public void deleteUser(long userId) {
		try {
			paymentDetailsRepo.deleteByUserId(userId);
			log.info("all payment record of this user is deleted ");
			userCouponMappingRepo.deleteByUserId(userId);
			log.info("all coupon mapping record of this user is deleted ");
			userRepository.deleteById(userId);
		} catch (Exception e) {
			throw new CantDeleteException("User can not be deleted..! Cause> " + e.getMessage());
		}
	}

	@Override
	public Boolean updateUserStatus(long userId, int userStatus) {

		User user = userRepository.findById(userId).get();

		user.setStatus(userStatus);
		if (userRepository.save(user) != null)
			return Boolean.TRUE;

		return Boolean.FALSE;
	}

	@Override
	public Boolean getUserStatus(String Email) {
		Optional<User> user = userRepository.findByEmail(Email);
		if (user.get() == null)
			return Boolean.FALSE;
		else {
			int status = user.get().getStatus();
			if (status == 1)
				return Boolean.TRUE;
			else
				return Boolean.TRUE;
		}
	}

	@Override
	public Boolean updateProfile(UserRequest userRequest) {
		User user = userRepository.findByEmail(userRequest.getRequestEmailId()).get();
		user.setFirstName(userRequest.getRequestFirstName());
		user.setLastName(userRequest.getRequestLastName());
		user.setEmail(userRequest.getRequestEmailId());
		user.setAddress(userRequest.getRequestAddress());
		user.setPhoneNo(userRequest.getRequestPhone());
		try {
			userRepository.save(user);
			return Boolean.TRUE;
		} catch (Exception e) {
			e.printStackTrace();
			return Boolean.FALSE;
		}
	}

	@Override
	public Boolean DeactivateAccount() {
		try {
			String UserId = CommonUtil.getCurrentUser();
			User user = userRepository.findByEmail(UserId).get();
			user.setStatus(0);
			userRepository.save(user);
			return Boolean.TRUE;
		} catch (Exception e) {
			throw new ActiveDeactiveException("Failed to deactive the user account:");
		}
	}

}
