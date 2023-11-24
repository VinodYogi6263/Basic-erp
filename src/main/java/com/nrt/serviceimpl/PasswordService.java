package com.nrt.serviceimpl;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.nrt.entity.PasswordHistory;
import com.nrt.entity.User;
import com.nrt.repository.PasswordHistoryRepository;
import com.nrt.repository.UserRepository;

@Service
public class PasswordService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordHistoryRepository passwordHistoryRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public Boolean changePassword(String username, String newPassword) {
		Optional<User> user = userRepository.findByEmail(username);
		if (!user.isPresent()) {
			throw new UsernameNotFoundException("User does Not Exits");
		}

		String newPasswordHash = passwordEncoder.encode(newPassword);

		List<PasswordHistory> passwordHistory = passwordHistoryRepository
				.findByUserIdOrderByCreatedAtDesc(user.get().getId());
		if (passwordHistory.size() >= 15) {
			for (int i = 14; i < passwordHistory.size(); i++) {
				passwordHistoryRepository.delete(passwordHistory.get(i));
			}
			passwordHistory = passwordHistory.subList(0, 15);
		}

		boolean passwordFoundInHistory = passwordHistory.stream().map(PasswordHistory::getPasswordHash)
				.anyMatch(oldPasswordHash -> passwordEncoder.matches(newPassword, oldPasswordHash));
		if (passwordFoundInHistory) {
			return Boolean.FALSE;
		}

		PasswordHistory newPasswordHistory = new PasswordHistory(user.get().getId(), newPasswordHash,
				LocalDateTime.now());
		passwordHistoryRepository.save(newPasswordHistory);
		Date date = Date.valueOf(new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()));
		user.get().setPassword(newPasswordHash);
		user.get().setPasswordUpdated(date);
		userRepository.save(user.get());
		return Boolean.TRUE;
	}
}
