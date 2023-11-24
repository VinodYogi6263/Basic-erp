package com.nrt.serviceimpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.nrt.entity.CartItem;
import com.nrt.entity.Category;
import com.nrt.entity.OrderDetails;
import com.nrt.entity.OrderedItemDetails;
import com.nrt.entity.Product;
import com.nrt.entity.SubCategory;
import com.nrt.exception.CantDeleteException;
import com.nrt.repository.CartItemRepository;
import com.nrt.repository.CategoryRepository;
import com.nrt.repository.OrderDetailsRepo;
import com.nrt.repository.OrderedItemsDetailsRepo;
import com.nrt.repository.ProductRepository;
import com.nrt.repository.SubCategoryRepository;
import com.nrt.request.ProductRequest;
import com.nrt.service.CartItemService;
import com.nrt.service.ProductService;
import com.nrt.util.CommonUtil;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class ProductServiceImpl implements ProductService {

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private SubCategoryRepository subCategoryRepository;

	@Value("${image.upload.path}")
	private String imageUploadPath;

	@Autowired
	CartItemService cartItemService;
	@Autowired
	OrderDetailsRepo OrderDetailsRepo;

	@Autowired
	OrderedItemsDetailsRepo orderedItemsDetailsRepo;
	@Autowired
	CartItemRepository cartItemRepository;

	@Override
	public List<Product> getAllProduct() {
		return productRepository.findAll();
	}

	@Override
	public Product GetProductById(Long id) {
		Optional<Product> findById = productRepository.findById(id);
		return findById.get();
	}

	@Override
	public void deleteProduct(Long id) {
		try {
			productRepository.deleteById(id);
		} catch (Exception e) {
			throw new CantDeleteException("Product can not be deleted..! Cause> " + e.getMessage());
		}

	}

	@Override
	public boolean updateProducts(Product productUpdate, MultipartFile file) {

		try {
			productUpdate.setShopkeeperId(CommonUtil.getCurrentUserDetails().getEmail());
			productUpdate.setImagePath(file.getOriginalFilename());
			Product updatedProduct = productRepository.save(productUpdate);

			// Files.copy(file.getInputStream(), destination,
			// StandardCopyOption.REPLACE_EXISTING);
			List<CartItem> cartItemsList = cartItemRepository.findAllByProduct(updatedProduct);

			for (CartItem cartIteml : cartItemsList) {
				CartItem cartItem = cartItemRepository.findById(cartIteml.getId()).get();
				cartItem.setTotal(cartItem.getQuantity() * updatedProduct.getSellingPrice());
				cartItem.setImagePath(updatedProduct.getImagePath());
				cartItem.setProduct(updatedProduct);
				cartItemRepository.save(cartItem);
				log.info("cartItem is updated " + cartItem.getProduct().getName());
			}

			return updatedProduct != null; // Return true if the save operation was successful

		} catch (Exception e) {
			e.printStackTrace(); // You can handle the exception as needed
			return false; // Return false if an exception occurs during the save operation
		}
	}

	@Override
	public boolean saveProduct(ProductRequest productRequest, MultipartFile file) {
		Product product = new Product();
		product.setDescription(productRequest.getDescription());
		product.setName(productRequest.getName());
		product.setMaxRetailPrice(productRequest.getMaxRetailPrice());
		product.setPurchasePrice(productRequest.getPurchasePrice());
		product.setSellingPrice(productRequest.getSellingPrice());
		product.setQuantity(productRequest.getQuantity());
		product.setImagePath(file.getOriginalFilename());
		product.setShopkeeperId(CommonUtil.getCurrentUser());
		if (productRepository.existsById(product.getId())) {
			return false;
		} else {
			String imagePath = imageUploadPath + file.getOriginalFilename();
			Path destination = Paths.get(imagePath);
			try {
				Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				e.printStackTrace();

			}

			Optional<SubCategory> subcategoryOption = subCategoryRepository.findById(productRequest.getSubCategoryId());
			Optional<Category> categoryOption = categoryRepository.findById(productRequest.getCategoryId());
			if (subcategoryOption.isPresent() && categoryOption.isPresent()) {
				List<SubCategory> subcategory = new ArrayList<>();
				subcategory.add(subcategoryOption.get());
				categoryOption.get().setSubcategories(subcategory);
				product.setCategory(categoryOption.get());
			}
			productRepository.save(product);
			return true;
		}
	}

	@Override
	public void decreaseProductQuantityCount(long orderId) {

		OrderDetails orderDetails = OrderDetailsRepo.findById(orderId).get();

		List<OrderedItemDetails> orderedItems = orderedItemsDetailsRepo.findByOrder(orderDetails);

		for (OrderedItemDetails orderedItem : orderedItems) {
			Product product = productRepository.findById(orderedItem.getProduct_Id()).get();

			if (product.getQuantity() > 0)
				product.setQuantity((int) (product.getQuantity() - orderedItem.getQuantity()));

			productRepository.save(product);

			orderDetails.setStatus("Accepted");
			OrderDetailsRepo.save(orderDetails);
		}

		log.info("products quantity has decreased as per quantity of ordered products");
	}

	@Override
	public void OrderCancelled(long orderId) {

		log.info("order cancelled and your refund amount we will credited within 7 days");
		// shopkeeper will so this manually to respective customer.
		OrderDetails orderDetails = OrderDetailsRepo.findById(orderId).get();

		List<OrderedItemDetails> orderedItems = orderedItemsDetailsRepo.findByOrder(orderDetails);

		for (OrderedItemDetails orderedItem : orderedItems) {
			Product product = productRepository.findById(orderedItem.getProduct_Id()).get();
			product.setQuantity((int) (product.getQuantity() + orderedItem.getQuantity()));
			productRepository.save(product);
		}

		orderDetails.setStatus("Cancelled");
		OrderDetailsRepo.save(orderDetails);
		log.info("products quantity has decreased as per quantity of ordered products");
	}

	@Override
	public List<Product> getAllProductOfOneShop() {
		return productRepository.findAllByShopkeeperId(CommonUtil.getCurrentUser());
	}

}
