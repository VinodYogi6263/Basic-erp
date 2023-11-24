package com.nrt.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nrt.entity.Category;
import com.nrt.entity.Product;
import com.nrt.entity.SubCategory;
import com.nrt.entity.User;
import com.nrt.repository.ProductRepository;
import com.nrt.request.ProductRequest;
import com.nrt.service.CategoryService;
import com.nrt.service.ProductService;
import com.nrt.service.SubCategoryService;
import com.nrt.util.CommonUtil;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Controller
public class ProductController {

	@Autowired
	private ProductService productService;

	@Autowired
	private CategoryService catagoryService;

	@Autowired
	private SubCategoryService subCatagoryService;
	@Autowired
	ProductRepository productRepository;

	// this method redirect Add Product page
	@GetMapping("/product")
	@PreAuthorize("hasRole('ADMIN') or hasRole('PRODUCT-ADD')")
	public ModelAndView defaultMethod(ModelAndView modelAndView) {
		List<Category> catagories = catagoryService.getAllCategory();
		modelAndView.addObject("catagories", catagories);
		List<SubCategory> subCatagories = subCatagoryService.getAllSubCategory();
		modelAndView.addObject("subCatagories", subCatagories);
		System.out.println(catagories);
		modelAndView.setViewName("/html/product/add_Product");
		return modelAndView;
	}

	// this method Add product
	@PostMapping("/saveProduct")
	@PreAuthorize("hasRole('ADMIN') or hasRole('PRODUCT-ADD')")
	public ModelAndView addProduct(@ModelAttribute("productRequest") ProductRequest productRequest,
			@RequestParam("file") MultipartFile file, ModelAndView modelAndView) {

		boolean b = productService.saveProduct(productRequest, file);// call sevice layer
		// saveProduct method
		if (!b) {
			modelAndView.addObject("errorMessage", "Product is already exists.");
			modelAndView.addObject("error", "An error occurred while processing your request. Please try again later.");
			modelAndView.setViewName("/html/product/error_message");
		} else {

			List<Product> products = productService.getAllProduct();
			modelAndView.addObject("products", products);
			modelAndView.setViewName("/html/product/list_product");
		}
		return modelAndView;
	}

	// this method find all product
	@GetMapping("/listProduct")
	@PreAuthorize("hasRole('ADMIN') or hasRole('PRODUCT-LIST')")
	public ModelAndView getAllProductsList(ModelAndView modelAndView) {

		List<Product> products = new ArrayList<Product>();

		User currentUser = CommonUtil.getCurrentUserDetails();
		if (currentUser.getRole().getName().equalsIgnoreCase("admin")) {
			products = productService.getAllProduct();
			modelAndView.setViewName("/html/product/list_product");
		} else {
			products = productService.getAllProductOfOneShop();
			modelAndView.setViewName("html/shopkeeper/dashboard.html");
		}

		modelAndView.addObject("products", products);
		return modelAndView;
	}

	// this method find product by id
	@GetMapping("/getProduct/")
	public ModelAndView getProduct(@RequestParam("id") Long id, ModelAndView modelAndView) {
		Product products = productService.GetProductById(id);

		modelAndView.addObject("getProductById", products);
		modelAndView.setViewName("/html/product/list_product");
		return modelAndView;
	}

	@GetMapping("/searchProduct")
	public ModelAndView getProductByName(@RequestParam("term") String searchTerm, ModelAndView modelAndView)
			throws JsonProcessingException {
		log.info(" searchProduct method called ");
		Product products = productRepository.findByName(searchTerm);
		modelAndView.addObject("products", products);
		modelAndView.setViewName("/html/product/list_product");
		log.info(" searchProduct method called  end" + products.getName());
		return modelAndView;
	}

	// this method delete product by id
	@GetMapping("/deleteProduct/")
	@PreAuthorize("hasRole('ADMIN') or hasRole('PRODUCT-DELETE')")
	public ModelAndView deleteProduct(@RequestParam("id") Long id, ModelAndView modelAndView) {
		productService.deleteProduct(id);
		return this.getAllProductsList(modelAndView);
	}

	// update product call by id
	@GetMapping("/updateProductById/")
	@PreAuthorize("hasRole('ADMIN') or hasRole('PRODUCT-UPDATE')")
	public ModelAndView updateProduct(@RequestParam("id") Long id, @ModelAttribute Product product,
			ModelAndView modelAndView) {
		product = productService.GetProductById(id);
		modelAndView.addObject("product", product);
		modelAndView.setViewName("/html/product/update_product"); // View name without extension
		return modelAndView;

	}

//	//update product 
	@PostMapping("/updateProduct")
	@PreAuthorize("hasRole('ADMIN') or hasRole('PRODUCT-UPDATE')")
	public ModelAndView updateProduct(@ModelAttribute("product") Product product,
			@RequestParam("file") MultipartFile file, ModelAndView modelAndView) {
		modelAndView.addObject("title", "Product update");
		modelAndView.addObject("message", "Successfull");
		modelAndView.addObject("details", "\"Congratulations! Product Update successfully !");
		modelAndView.addObject("error", "An error occurred while processing your request. Please try again later.");
		modelAndView.setViewName(productService.updateProducts(product, file) ? "/html/product/response_page"
				: "/html/product/error_message");
		return modelAndView;

	}

	@GetMapping(value = "/images/{imageName}", produces = MediaType.APPLICATION_ATOM_XML_VALUE)
	public void getImage(@PathVariable String imageName, HttpServletResponse response) throws IOException {
		ClassLoader classLoader = getClass().getClassLoader();
		String resourceFolderPath = classLoader.getResource("static/images/").getPath();
		String imageFilePath = resourceFolderPath + imageName;
		File imageFile = new File(imageFilePath);
		if (!imageFile.exists() || !imageFile.isFile()) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		response.setContentType(MediaType.IMAGE_JPEG_VALUE);
		try (InputStream inputStream = new FileInputStream(imageFile);
				ServletOutputStream outputStream = response.getOutputStream()) {

			byte[] buffer = new byte[1024];
			int bytesRead;
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
			}
		}
	}
}
