package com.nrt.serviceimpl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nrt.entity.CartItem;
import com.nrt.entity.Product;
import com.nrt.exception.CantDeleteException;
import com.nrt.exception.ProductOutOfStockException;
import com.nrt.repository.CartItemRepository;
import com.nrt.repository.ProductRepository;
import com.nrt.service.CartItemService;
import com.nrt.util.CommonUtil;

import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class CartItemServiceImpl implements CartItemService {

	@Autowired
	CartItemRepository cartItemRepository;
	@Autowired
	ProductRepository productRepository;

	@Override
	public Boolean addItemTocart(long productId, int quantity, String userId) {
		Optional<Product> product = productRepository.findById(productId);

		Boolean isOutOfStock = checkOutOfStock(product.get(), quantity);
		if (isOutOfStock)
			throw new ProductOutOfStockException("product is out of stock or product stock is not enough");

		CartItem alreadyItemAdded = cartItemRepository.findByProductIdAndUserId(productId, userId);
		if (alreadyItemAdded == null) {

			CartItem newCartItem = new CartItem();
			if (product.isPresent()) {
				log.info("product found with id: " + productId);
				newCartItem.setProduct(product.get());
				newCartItem.setQuantity(quantity);
				newCartItem.setTotal(quantity * product.get().getSellingPrice());
				newCartItem.setUserId(userId);
				newCartItem.setImagePath(product.get().getImagePath());
				log.info("user id is :" + userId);
				CartItem savedCartItem = cartItemRepository.save(newCartItem);
				if (savedCartItem != null) {
					log.info(" new product is added to cart list");
					return Boolean.TRUE;
				} else
					return Boolean.TRUE;
			} else {
				log.info(" product is not found ");
				return Boolean.FALSE;
			}
		} else {
			log.info("product exits already in cart list");
			long addedQuantity = alreadyItemAdded.getQuantity();
			alreadyItemAdded.setQuantity(addedQuantity + quantity);

			CartItem savedCartItem = cartItemRepository.save(alreadyItemAdded);
			if (savedCartItem != null) {
				log.info("product count is increased & added to cart list");
				return Boolean.TRUE;
			} else
				return Boolean.FALSE;
		}
	}

	@Override
	public List<CartItem> getAllCartItems() {
		cartItemRepository.findAll();
		return cartItemRepository.findAll();
	}

	@Override
	public void deleteByCartId(long cartId) {
		try {
			cartItemRepository.deleteById(cartId);
		} catch (Exception e) {
			throw new CantDeleteException("cart item can not be deleted..! Cause> " + e.getMessage());
		}
	}

	@Override
	public List<CartItem> getUsersAllCartItems(String userId) {
		return cartItemRepository.findByUserId(userId);

	}

	public static Boolean checkOutOfStock(Product product, int quantity) {
		if (product.getQuantity() == 0)
			return Boolean.TRUE;
		if (product.getQuantity() != 0)
			if (product.getQuantity() < quantity)
				return Boolean.TRUE;
		return Boolean.FALSE;
	}
	
	@Override
	@Transactional
	public void deleteCartItemsByUserId() {
		try {
			cartItemRepository.deleteAllByUserId(CommonUtil.getCurrentUser());
			log.info(" all cart items are removed from your cart list");
		} catch (Exception e) {
			throw new CantDeleteException("cart item list can not be deleted..! Cause> " + e.getMessage());
		}
	}




}
