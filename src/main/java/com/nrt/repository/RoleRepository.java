package com.nrt.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nrt.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
	List<Role> findAll();

	void deleteById(long roleId);

	Role findByName(String name);

}