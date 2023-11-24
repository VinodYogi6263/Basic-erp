package com.nrt.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nrt.entity.PasswordHistory;


public interface PasswordHistoryRepository extends JpaRepository<PasswordHistory, Long> {
	List<PasswordHistory> findByUserIdOrderByCreatedAtDesc(Long userId);
}
