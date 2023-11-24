package com.nrt.razorpayApi;

import java.security.Principal;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nrt.Email.EmailSender;
import com.nrt.entity.CartItem;
import com.nrt.entity.PaymentDetails;
import com.nrt.entity.User;
import com.nrt.exception.EmailMessagingException;
import com.nrt.pdfGenerator.MyPdfGenerator;
import com.nrt.repository.PaymentDetailsRepo;
import com.nrt.repository.UserRepository;
import com.nrt.request.UserRequest;
import com.nrt.service.ProductService;
import com.nrt.service.UserService;
import com.nrt.serviceimpl.CartItemServiceImpl;
import com.nrt.serviceimpl.OrderService;
import com.nrt.util.CommonUtil;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class RazorpayService {

	@Value("${razorpay.api.key}")
	private String apiKey;

	@Value("${razorpay.api.secret}")
	private String apiSecret;

	@Autowired
	PaymentDetailsRepo paymentDetailsRepo;
	@Autowired
	UserRepository userRepository;

	@Autowired
	EmailSender emailSender;
	@Autowired
	CartItemServiceImpl cartItemServiceImpl;
	@Autowired
	MyPdfGenerator pdfGenerator;
	@Autowired
	TemplateEngine templateEngine;
	@Autowired
	UserService userService;
	@Autowired
	OrderService orderService;
	
	@Autowired
	ProductService productService;

	public Order createPaymentOrder(double amount) throws Exception {

		log.info("method payment order is called");
		RazorpayClient razorpayClient = new RazorpayClient(apiKey, apiSecret);
		JSONObject options = new JSONObject();
		options.put("amount", amount * 100); // Amount in paise
		options.put("currency", "INR");
		options.put("receipt", "txt_784503");

		Order order = razorpayClient.orders.create(options);
		log.info("order details : " + order.toString());
		return order;
	}

	public void createRecord(Order response, Principal principal)
			throws JsonMappingException, JsonProcessingException, JSONException {

		log.info("create record method is called  :" + response.toString());
		PaymentDetails paymentDetails = new PaymentDetails();

		ObjectMapper objectMapper = new ObjectMapper();

		paymentDetails = objectMapper.readValue(response.toJson().toString(), PaymentDetails.class);
		User user = userRepository.findByEmail(principal.getName()).get();
		paymentDetails.setUser(user);
		paymentDetails.setAmount(paymentDetails.getAmount() / 100);
		paymentDetails.setAmount_due(paymentDetails.getAmount_due() / 100);
		paymentDetailsRepo.save(paymentDetails);

	}

	public void updateRecord(paymentResponse response) {
		orderService.createOrder();
		
		log.info("update method is called  :" + response.toString());
		PaymentDetails paymentDetails = paymentDetailsRepo.findById(response.getRazorpay_order_id());
		paymentDetails.setId(response.getRazorpay_order_id());
		paymentDetails.setAmount_due(0);
		paymentDetails.setStatus("success");
		paymentDetails.setId(response.getRazorpay_payment_id());
		paymentDetails.setSignature(response.getRazorpay_signature());
		paymentDetails.setAmount_paid(paymentDetails.getAmount());
		paymentDetailsRepo.save(paymentDetails);
		

	}

	public void sendInvoiceToEmail() {
		try {

			UserRequest userData = userService.getUserByEmail(CommonUtil.getCurrentUser());
			List<CartItem> cartItems = cartItemServiceImpl.getUsersAllCartItems(CommonUtil.getCurrentUser());

			int tatalAmount = 0;
			for (CartItem item : cartItems) {
				tatalAmount += item.getTotal();
			}
			// Prepare the context with dynamic data
			Context context = new Context();
			context.setVariable("user", userData);
			context.setVariable("items", cartItems);
			context.setVariable("totalAmount", tatalAmount);

			String filePath = "/html/buyOrder/invoiceEmailAttachment";
			// Process the Thymeleaf template
			String htmlContent = templateEngine.process(filePath, context);
			// Generate the PDF
			byte[] pdfByteData = pdfGenerator.htmlContentToPDF(htmlContent);

			emailSender.sendEmail(CommonUtil.getCurrentUser(), "Order Placed Succefully",
					"/html/email/invoice-template", "invoice.pdf", pdfByteData);
		} catch (Exception e) {
			e.printStackTrace();
			throw new EmailMessagingException("failed to send the email");
		}
	}

}
