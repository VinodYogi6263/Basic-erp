package com.nrt.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.nrt.entity.Role;
import com.nrt.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

	public Optional<User> findByEmail(String email);

	public List<User> findByRole(Role role);

	public boolean existsByEmail(String string);

}
