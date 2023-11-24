package com.nrt.util;

import java.security.SecureRandom;

import org.springframework.stereotype.Component;

@Component
public class RandomPasswordGeneratorWithPattern {
	private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$%^&+=";
	private static SecureRandom random = new SecureRandom();

	public static String generateRandomPassword() {
		StringBuilder password = new StringBuilder();

		// Ensure at least one digit, one lowercase, one uppercase, and one special
		// character
		password.append(CHARACTERS.charAt(random.nextInt(10))); // Digit
		password.append(CHARACTERS.charAt(random.nextInt(26)) + 10); // Lowercase letter
		password.append(CHARACTERS.charAt(random.nextInt(26)) + 36); // Uppercase letter
		password.append(CHARACTERS.charAt(random.nextInt(7)) + 62); // Special character

		// Fill the rest of the password length
		for (int i = 4; i < 8; i++) {
			int randomIndex = random.nextInt(CHARACTERS.length());
			char randomChar = CHARACTERS.charAt(randomIndex);
			password.append(randomChar);
		}

		// Shuffle the characters in the password
		for (int i = 0; i < password.length(); i++) {
			int randomIndex = random.nextInt(password.length());
			char temp = password.charAt(i);
			password.setCharAt(i, password.charAt(randomIndex));
			password.setCharAt(randomIndex, temp);
		}

		return password.toString();
	}
	
}