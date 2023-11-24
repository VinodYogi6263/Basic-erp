package com.nrt.razorpayApi;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.nrt.entity.CartItem;
import com.nrt.pdfGenerator.MyPdfGenerator;
import com.nrt.request.UserRequest;
import com.nrt.serviceimpl.CartItemServiceImpl;
import com.nrt.serviceimpl.UserServiceImpl;
import com.nrt.util.CommonUtil;
import com.razorpay.Order;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Controller
@RequestMapping("/pay")
public class PaymentController {

	@Autowired
	CartItemServiceImpl cartItemServiceImpl;
	@Autowired
	UserServiceImpl userServiceImpl;
	@Autowired
	RazorpayService razorpayService;

	@Autowired
	MyPdfGenerator pdfGenerator;
	
	@GetMapping("/getpay")
	public ModelAndView getPayment(@RequestParam("price") String amount, ModelAndView modelAndView,
			Principal pricipal) {
		double amountd = Double.parseDouble(amount);
		Order orderDetails = null;
		try {
			orderDetails = razorpayService.createPaymentOrder(amountd);
			log.info("order Details is :" + orderDetails);
			razorpayService.createRecord(orderDetails, pricipal);
			modelAndView.addObject("orderId", orderDetails.get("id").toString());

			log.info("order id is :" + orderDetails.get("id"));

		} catch (Exception e) {
			log.info(" Exception occurd Details is :" + e.getMessage());
		}
		

		modelAndView.setViewName("/html/buyOrder/razorpay");
		return modelAndView;
	}

	@PostMapping("/razorpay-callback")
	public String handleRazorpayCallback(@RequestBody paymentResponse response) {

		log.info("callback method called " + response.toString());
		log.info(" razorpay_order id  " + response.razorpay_order_id);
		// Save payment data in the database
		razorpayService.updateRecord(response);
        
		razorpayService.sendInvoiceToEmail();
		// Check if the payment was successful
		System.out.println("redirecting to page controller");
		return "redirect:/pay/invoice";
	}

	@GetMapping("/invoice")
	public ModelAndView invoicePage(ModelAndView modelAndView) {
		List<CartItem> cartItems = cartItemServiceImpl.getUsersAllCartItems(CommonUtil.getCurrentUser());
		long cartTotal = 0;

		if (cartItems != null)
			for (CartItem cartItem : cartItems) {
				cartTotal += cartItem.getQuantity() * cartItem.getProduct().getSellingPrice();
			}
		UserRequest userDetails = userServiceImpl.getUserByEmail(CommonUtil.getCurrentUser());
       
		log.info("user details is :" + userDetails.toString());
		modelAndView.addObject("user", userDetails);
		modelAndView.addObject("totalAmount", cartTotal);
		modelAndView.addObject("items", cartItems);
		modelAndView.setViewName("/html/buyOrder/invoice");
		return modelAndView;
	}

	@PostMapping("/downloadInvoice")
	public ResponseEntity<byte[]> invoiceDownloader(@RequestParam("htmlContent") String htmlContent) {
		byte[] byteData = pdfGenerator.htmlContentToPDF(htmlContent);

		if (byteData == null)
			return new ResponseEntity<>(null, null, HttpStatus.FAILED_DEPENDENCY);
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_PDF);
		headers.setContentDispositionFormData("attachment", "invoice.pdf");

		return new ResponseEntity<>(byteData, headers, HttpStatus.OK);
	}

}
