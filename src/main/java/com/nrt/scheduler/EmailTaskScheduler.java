package com.nrt.scheduler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nrt.Email.EmailSender;
import com.nrt.entity.Product;
import com.nrt.exception.EmailMessagingException;
import com.nrt.repository.ProductRepository;

import jakarta.mail.MessagingException;

@Service
public class EmailTaskScheduler implements Runnable {

	@Autowired
	ProductRepository productRepository;
	@Autowired
	private EmailSender EmailSender;

	private static final Logger log = LoggerFactory.getLogger(EmailTaskScheduler.class);

	@Override
	public void run() {
		// TODO Auto-generated method stub
		log.info("Scheduled task executed.");

		try {

			Set<String> ShopkeeperList = new HashSet<String>();

			List<Product> allProdcuts = productRepository.findAll();

			for (Product product : allProdcuts) {
				if (product.getQuantity() == 0)
					ShopkeeperList.add(product.getShopkeeperId());

			}

			for (String shopkeeper : ShopkeeperList) {

				List<Product> allshopkeeperProdcuts = productRepository.findAllByShopkeeperId(shopkeeper);

				List<Product> outOfStockProductsList = new ArrayList<Product>();

				for (Product product : allshopkeeperProdcuts) {
					if (product.getQuantity() == 0) {
						outOfStockProductsList.add(product);
						log.info("prodcut" + product.toString() + "is added to out of stock list of shoopkeeper id :"
								+ product.getShopkeeperId());
					}
				}

				try {
					Map<String, Object> listInMap = new HashMap<String, Object>();
					listInMap.put("productList", outOfStockProductsList);
					listInMap.put("username", shopkeeper);
					EmailSender.sendEmail(shopkeeper, "Products are out of stocks",
							"/html/email/outOfStock-template.html", listInMap);
				} catch (MessagingException e) {
					throw new EmailMessagingException(
							"failed to send list of products out of stocks notifications to shopkeeper");
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("failed to execute this task scheduler");
		}
	}

}