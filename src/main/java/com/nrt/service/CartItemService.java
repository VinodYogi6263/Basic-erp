package com.nrt.service;

import java.util.List;

import com.nrt.entity.CartItem;

public interface CartItemService {

	public Boolean addItemTocart(long id, int quantity,String userId);

	public List<CartItem> getAllCartItems();

	public void deleteByCartId(long cartId);

	public  List<CartItem> getUsersAllCartItems(String userId);

	void deleteCartItemsByUserId();
		
}
