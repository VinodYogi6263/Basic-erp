package com.nrt.request;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CouponRequest {

	private long couponId;

	private String couponCode;

	private int couponType;

	private long couponAmount;

	private LocalDate couponExpiresAt;

	private LocalDate couponActivetAt;

	private long couponIfAmountAbove;

	private String couponDescription;

	private long couponApplyPerUser;

}
