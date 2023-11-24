package com.nrt.responce;

import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
public class ApplyCouponResponse {
	
	private Boolean isApplied;
	private long cartTotalAfterCouponApplied;
	private String message;

}
