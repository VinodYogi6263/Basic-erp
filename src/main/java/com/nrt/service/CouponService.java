package com.nrt.service;

import java.util.List;

import org.springframework.stereotype.Component;

import com.nrt.entity.Coupon;
import com.nrt.request.CouponRequest;
import com.nrt.responce.ApplyCouponResponse;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.NotNull;

@Component
public interface CouponService {

	public Boolean Register(CouponRequest couponRequest);

	public CouponRequest getCouponById(Long couponId);

	public Boolean Delete(long couponId);

	public List<Coupon> FindAllCoupon();
	
	public ApplyCouponResponse applyCoupon (String code , String Id,HttpSession session);

	public void addCouponCount(String couponCode, @NotNull String userId);

	public Boolean updateCouponStatus(Long couponId, int status);
	

}
