package com.nrt.razorpayApi;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
public class paymentResponse {

	public String razorpay_payment_id;
	public String razorpay_order_id;
	public String razorpay_signature;
}
