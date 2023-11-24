package com.nrt.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.nrt.entity.Permissions;

public interface PermissionRepository extends JpaRepository<Permissions, Long> {

	List<Permissions> findByRoleId(long roleId);

	List<Permissions> findByNameIn(List<String> permissions);

	List<Permissions> findByRoleName(String roleName);
	

	@Modifying
    @Query(value = "DELETE FROM role WHERE user_id = :userId", nativeQuery = true)
    void deleteRolesByUserId(long userId);

	boolean existsByRoleName(String string);

	
	
}
