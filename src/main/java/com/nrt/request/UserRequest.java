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

	private String requestFirstName;
	private String requestLastName;
	private String requestPhone;
	private String requestEmialId;

	private String dP;

}
