package com.nrt.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nrt.entity.PaymentDetails;

import jakarta.transaction.Transactional;

public interface PaymentDetailsRepo extends JpaRepository<PaymentDetails, Long>{

	PaymentDetails findById(String razorpay_order_id);

	@Transactional
	void deleteByUserId(long userId);
	

}
