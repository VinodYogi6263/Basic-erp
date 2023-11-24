package com.nrt.service;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.nrt.entity.User;
import com.nrt.request.LoginRequest;
import com.nrt.request.UserRequest;
import com.nrt.responce.LoginResponce;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface UserService {

	public ResponseEntity<User> saveData(UserRequest userRequest);

	public ResponseEntity<User> getUserById(long userId);

	public Boolean updatePassword(String oldPassword, String newPassword);

	public ResponseEntity<LoginResponce> generateToken(LoginRequest loginRequest);

	public UserRequest getUserByEmail(String Email);

	public Boolean SendOTP(String email, HttpServletResponse response);

	public Boolean OTPVelidation(String otp, HttpServletRequest request);

	public Boolean ForgotPassword(String newPassword, HttpServletRequest request, HttpServletResponse response);

	public Boolean saveDP(MultipartFile file);

	public List<UserRequest> getAllUsersList();

	public void deleteUser(long userId);

	public Boolean updateUserStatus(long userId, int userStatus);

	public Boolean getUserStatus(String userId);

	public Boolean updateProfile(UserRequest userRequest);

	void assignDefaultRoleToUser(String email);

	public Boolean DeactivateAccount();

}