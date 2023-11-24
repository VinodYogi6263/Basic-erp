package com.nrt.serviceimpl;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import com.nrt.entity.CartItem;
import com.nrt.entity.OrderDetails;
import com.nrt.entity.OrderedItemDetails;
import com.nrt.entity.Product;
import com.nrt.entity.User;
import com.nrt.exception.ProductOutOfStockException;
import com.nrt.repository.OrderDetailsRepo;
import com.nrt.repository.OrderedItemsDetailsRepo;
import com.nrt.repository.ProductRepository;
import com.nrt.repository.UserRepository;
import com.nrt.service.CartItemService;
import com.nrt.service.UserService;
import com.nrt.util.CommonUtil;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class OrderService {

	@Autowired
	CartItemServiceImpl cartItemServiceImpl;
	@Autowired
	UserServiceImpl userServiceImpl;
	@Autowired
	ProductServiceImpl productServiceImpl;

	@Autowired
	UserService userService;
	@Autowired
	ProductRepository productRepository;
	@Autowired
	OrderDetailsRepo orderDetailsRepo;
	@Autowired
	CartItemService cartItemService;
	@Autowired
	UserRepository userRepository;

	@Autowired
	private OrderedItemsDetailsRepo orderedItemsDetailsRepo;

	public ModelAndView getOrderSummary(ModelAndView modelAndView, String code, String discountedPrice,
			String totalAmount) {
		int amount = 0;
		String UserId = CommonUtil.getCurrentUser();

		if (discountedPrice != null)
			if (!discountedPrice.isEmpty())
				amount = Integer.parseInt(discountedPrice);
			else
				amount = Integer.parseInt(totalAmount);
		return this.getSummary(modelAndView, code, amount, cartItemServiceImpl.getUsersAllCartItems(UserId));

	}

	public ModelAndView getSingleItemOrderSummary(ModelAndView modelAndView, String quantity, String productId) {
		long productID = Long.parseLong(productId);
		int Quantity = Integer.parseInt(quantity);
		Product product = productServiceImpl.GetProductById(productID);

		Boolean isOutOfStock = CartItemServiceImpl.checkOutOfStock(product, Quantity);
		if (isOutOfStock)
			throw new ProductOutOfStockException("product is out of stock or product stock is not enough");

		int amount = Quantity * product.getSellingPrice();

		CartItem cartItem = new CartItem();
		cartItem.setImagePath(product.getImagePath());
		cartItem.setTotal(amount);
		cartItem.setProduct(product);
		cartItem.setQuantity(Quantity);
		return this.getSummary(modelAndView, null, amount, cartItem);
	}

	public ModelAndView getSummary(ModelAndView modelAndView, String code, int amount, Object product) {
		String UserId = CommonUtil.getCurrentUser();
		modelAndView.addObject("cartItems", product);
		modelAndView.addObject("payableAmount", amount);

		modelAndView.addObject("code", code);
		modelAndView.addObject("user", userServiceImpl.getUserByEmail(UserId));
		modelAndView.setViewName("/html/buyOrder/orderSummary");

		return modelAndView;
	}
	
	
	
	@SuppressWarnings("deprecation")
	public void createOrder() {
		// create order request and also delete the cart Items for each vendors belongs
		// to product
		
		User userDetails = userRepository.findByEmail(CommonUtil.getCurrentUser()).get();

		List<CartItem> itemsList = cartItemServiceImpl.getUsersAllCartItems(userDetails.getEmail());
		
		Set<String> shopkeeperList = new HashSet<>();

		long totalAmount = 0;

		for (CartItem items : itemsList) {
			shopkeeperList.add(items.getProduct().getShopkeeperId());

		}

		for (String shopkeeper : shopkeeperList) {
  
			List<OrderedItemDetails> orderedItemsList = new ArrayList<>();
			
			OrderDetails order = new OrderDetails();
			
			
			order.setStatus("pending");
			order.setUserId(CommonUtil.getCurrentUser());
			order.setCreated_At(new Date(new java.util.Date().getDate()));
			order.setShippingAddress(userDetails.getAddress());
			
			for (CartItem items : itemsList) {
				totalAmount += items.getTotal();

				if(items.getProduct().getShopkeeperId().equalsIgnoreCase(shopkeeper))
				{
					totalAmount += items.getTotal();
				OrderedItemDetails orderedItemDetails = new OrderedItemDetails();

				orderedItemDetails.setProduct_Id(items.getProduct().getId());
				orderedItemDetails.setProductName(items.getProduct().getName());
				orderedItemDetails.setPrice(items.getProduct().getSellingPrice());
				orderedItemDetails.setImagePath(items.getImagePath());
				orderedItemDetails.setQuantity(items.getQuantity());
				orderedItemDetails.setTotal(items.getTotal());
				orderedItemDetails.setOrder(order);
				orderedItemsList.add(orderedItemDetails);
				
				log.info("added orderedItemDetails  :"+orderedItemDetails);
				}
				
				order.setShopkeeperId(shopkeeper);
				order.setUserName(userDetails.getFirstName() + userDetails.getLastName());
				order.setOrderedItems(orderedItemsList);
				order.setTotalAmount(totalAmount);
			
				orderDetailsRepo.save(order);
			
				log.info("created order for shopkeeper :"+shopkeeper);
			}

		}

		// delete the cart items list
		cartItemService.deleteCartItemsByUserId();
	}

	
	
	
	public List<OrderedItemDetails> getOneOrderDetails(OrderDetails order) {
		return orderedItemsDetailsRepo.findByOrder(order);
	}

	public List<OrderDetails> getAllOrderDetails() {
	String	shopkeeperId = CommonUtil.getCurrentUser();
		return orderDetailsRepo.findAllByShopkeeperId(shopkeeperId);
	}

	public List<OrderDetails> getAllUserOrderDetails() {
		return orderDetailsRepo.findAllByUserId(CommonUtil.getCurrentUser());
	}

}
