package com.nrt.request;

import org.springframework.stereotype.Component;

import com.nrt.entity.Product;

import lombok.Data;

@Data
@Component
public class CartItemRequest {

	private Product requestProduct;
	private long quantity;
	private double total;
}
