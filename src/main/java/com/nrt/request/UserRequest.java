package com.nrt.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserRequest {
	private long RequestId;
	private String requestFirstName;
	private String requestLastName;
	private String requestPhone;
	private String requestRole;
	private String requestEmailId;
	private int requestStatus;
	private String dP;
	private String requestAddress;

}
