package com.nrt.serviceimpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nrt.entity.Permissions;
import com.nrt.entity.Role;
import com.nrt.entity.User;
import com.nrt.exception.CantDeleteException;
import com.nrt.repository.PermissionRepository;
import com.nrt.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class PermissionService {

	@Autowired
	private PermissionRepository permissionRepository;
	@Autowired
	UserRepository userRepository;

	@Transactional
	public void deleteRolesByUserId(long userId) {
		try {
			permissionRepository.deleteRolesByUserId(userId);
		} catch (Exception e) {
			throw new CantDeleteException("Permission can not be deleted..! Cause> " + e.getMessage());
		}
		permissionRepository.deleteRolesByUserId(userId);
	}

	
	public List<String> getAllPermissionsList(long userId) {
		List<Permissions> AllpermissionsList = permissionRepository.findByRoleId(userId);
		List<String> permissionList = new ArrayList<String>();
		for (Permissions permissions : AllpermissionsList) {
			permissionList.add(permissions.getName());
		}
		return permissionList;
	}

	
	public List<String> getPermissionsByUserId(Long userId) {
		List<String> permissions = new ArrayList<>();

		User user = userRepository.findById(userId).get();
		if (user != null) {
			// Get the user's role
			Role role = user.getRole();
			if (role != null) {
				// Get the permissions associated with the role
				List<Permissions> rolePermissions = role.getPermissions();
				for (Permissions permission : rolePermissions) {
					permissions.add(permission.getName());
				}
			}
		}
		return permissions;
	}
}