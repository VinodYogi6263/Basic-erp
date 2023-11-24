package com.nrt.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.nrt.entity.Product;
import com.nrt.request.ProductRequest;

public interface ProductService {
	public boolean saveProduct(ProductRequest productRequest, MultipartFile file);

	public List<Product> getAllProduct();

	public Product GetProductById(Long id);

	public void deleteProduct(Long id);

	public boolean updateProducts(Product product, MultipartFile file);

	public void decreaseProductQuantityCount(long orderId);

	void OrderCancelled(long orderId);

	public List<Product> getAllProductOfOneShop();
}
