package com.nrt.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.nrt.entity.Catagory;
import com.nrt.entity.Product;
import com.nrt.entity.SubCatagory;
import com.nrt.request.ProductRequest;
import com.nrt.service.CatagoryService;
import com.nrt.service.ProductService;
import com.nrt.service.SubCatagoryService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@PreAuthorize("hasRole('PRODUCT')")
public class ProductController {

	@Autowired
	private ProductService productService;

	@Autowired
	private CatagoryService catagoryService;

	@Autowired
	private SubCatagoryService subCatagoryService;

	// this method redirect Add Product page
	@RequestMapping("/product")
	public ModelAndView defaultMethod(ModelAndView modelAndView) {
		List<Catagory> catagories = catagoryService.getAllCatagory();
		modelAndView.addObject("catagories", catagories);
		List<SubCatagory> subCatagories = subCatagoryService.getAllSubCatagory();
		modelAndView.addObject("subCatagories", subCatagories);
		System.out.println(catagories);
		modelAndView.setViewName("/html/product/add_Product");
		return modelAndView;
	}

	// this method Add product
	@RequestMapping("/saveProduct")
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
	public ModelAndView findProduct(ModelAndView modelAndView) {
		List<Product> products = productService.getAllProduct();
		modelAndView.addObject("products", products);
		modelAndView.setViewName("/html/product/list_product");
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

	// this method delete product by id
	@GetMapping("/deleteProduct/")
	public ModelAndView deleteProduct(@RequestParam("id") Long id, ModelAndView modelAndView) {

		productService.deleteProduct(id);

		List<Product> products = productService.getAllProduct();
		modelAndView.addObject("products", products);
		modelAndView.setViewName("/html/product/list_product");
		return modelAndView;
	}

	// update product call by id
	@GetMapping("/updateProductById/")
	public ModelAndView updateProduct(@RequestParam("id") Long id, @ModelAttribute Product product,
			ModelAndView modelAndView) {
		product = productService.GetProductById(id);
		modelAndView.addObject("product", product);
		modelAndView.setViewName("/html/product/update_product"); // View name without extension
		return modelAndView;

	}

//	//update product 
	@RequestMapping("/updateProduct")
	public ModelAndView updateProduct(@ModelAttribute("product") Product product,
			@RequestParam("file") MultipartFile file, ModelAndView modelAndView) {
		modelAndView.addObject("title", "Product update");
		modelAndView.addObject("message", "Successfull");
		modelAndView.addObject("details", "\"Congratulations! Product Update successfully !");
		modelAndView.addObject("error", "An error occurred while processing your request. Please try again later.");
		modelAndView.setViewName(productService.updateProducts(product, file) ? "/html/product/response_message"
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
