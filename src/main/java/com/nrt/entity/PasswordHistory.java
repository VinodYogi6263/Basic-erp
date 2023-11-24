package com.nrt.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordHistory {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private Long userId;
	private String passwordHash;
	private LocalDateTime createdAt = LocalDateTime.now();

	public PasswordHistory(Long userId, String passwordHash, LocalDateTime createdAt) {
		super();
		this.userId = userId;
		this.passwordHash = passwordHash;
		this.createdAt = createdAt;
	}

}
