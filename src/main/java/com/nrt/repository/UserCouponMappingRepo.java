package com.nrt.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nrt.entity.Coupon;
import com.nrt.entity.User;
import com.nrt.entity.UserCouponMapping;

import jakarta.transaction.Transactional;

public interface UserCouponMappingRepo extends JpaRepository<UserCouponMapping, Long>{
      

	UserCouponMapping findByUserAndCoupon(User user, Coupon coupon);
	
	UserCouponMapping findByCoupon(Coupon coupon);
	
    @Transactional
    void deleteByUserId(Long userId);
}
