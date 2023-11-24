package com.nrt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.nrt.entity.Coupon;


public interface CouponRepository extends JpaRepository<Coupon, Long> {
	
	Coupon findByCode(String code);

	Coupon findAllByCouponId(Long couponId);
	
}
