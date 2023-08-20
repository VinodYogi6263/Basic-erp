package com.nrt.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UpdateRequest {
	private String userId;
	private String oldPassword;
	private String newPassword;
	private String confirmPassword;
}
