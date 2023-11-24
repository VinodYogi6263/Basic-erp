package com.nrt.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.nrt.entity.Category;
import com.nrt.entity.SubCategory;
import com.nrt.exception.CantDeleteException;
import com.nrt.request.CategoryRequest;
import com.nrt.service.CategoryService;
import com.nrt.service.SubCategoryService;

@Controller
public class CategoryController {

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private SubCategoryService subCategoryService;

	// Call for adding the category
	@GetMapping("/category")
	@PreAuthorize("hasRole('ADMIN')  or hasRole('Category-ADD')")
	public ModelAndView defaultMethod(ModelAndView modelAndView) {
		List<SubCategory> subCategories = subCategoryService.getAllSubCategory();
		modelAndView.addObject("subCategories", subCategories);
		modelAndView.setViewName("/html/product/add_category");
		return modelAndView;
	}

	// call for save product category
	@PostMapping("/saveCategory")
	@PreAuthorize("hasRole('ADMIN')  or hasRole('Category-LIST')")
	public ModelAndView addProduct(@ModelAttribute("categoryRequest") CategoryRequest categoryRequest,
			ModelAndView modelAndView) {
		boolean b = categoryService.saveCategory(categoryRequest);// call sevice layer saveProduct method
		if (!b) {
			modelAndView.addObject("errorMessage", "This type of Category is already exists.");
			modelAndView.addObject("error", "An error occurred while processing your request. Please try again later.");
			modelAndView.setViewName("/html/product/error_message");
		} else {
			List<Category> categories = categoryService.getAllCategory();
			modelAndView.addObject("categories", categories);
			modelAndView.setViewName("/html/product/list_of_category");
		}
		return modelAndView;
	}

	// this method call for find all category
	@GetMapping("/listCategory")
	@PreAuthorize("hasRole('ADMIN')  or hasRole('Category-LIST')")
	public ModelAndView findProduct(ModelAndView modelAndView) {
		List<Category> categories = categoryService.getAllCategory();
		modelAndView.addObject("categories", categories);
		modelAndView.setViewName("/html/product/list_of_category");
		return modelAndView;
	}

	// this method call for delete category by id
	@GetMapping("/deleteCategory/")
	@PreAuthorize("hasRole('ADMIN')  or hasRole('Category-DELETE')")
	public ModelAndView deleteProduct(@RequestParam("id") Long id, ModelAndView modelAndView) {
		try {
			categoryService.deleteCategory(id);
		} catch (Exception e) {
			throw new CantDeleteException("Category can not be deleted..! Cause> " + e.getMessage());
		}
		List<Category> categories = categoryService.getAllCategory();
		modelAndView.addObject("categories", categories);
		modelAndView.setViewName("/html/product/list_of_category");
		return modelAndView;
	}

	// this method call for updating category by its id
	@GetMapping("/updateByCategoryId")
	@PreAuthorize("hasRole('ADMIN')  or hasRole('Category-UPDATE')")
	public ModelAndView updateProduct(@RequestParam("id") Long id, @ModelAttribute Category category,
			ModelAndView modelAndView) {
		category = categoryService.getCategoryById(id);
		modelAndView.addObject("categories", category);
		modelAndView.setViewName("/html/product/update_category"); // View name without extension
		return modelAndView;

	}

//	 this method call for update the category  
	@PostMapping("/updateCategory")
	@PreAuthorize("hasRole('ADMIN')  or hasRole('Category-UPDATE')")
	public ModelAndView updateProduct(@ModelAttribute Category category, ModelAndView modelAndView) {
		modelAndView.addObject("title", "Category update");
		modelAndView.addObject("message", "Successfull");
		modelAndView.addObject("details", "\"Congratulations! Category Update successfully !");
		modelAndView.addObject("error", "An error occurred while processing your request. Please try again later.");
		modelAndView.setViewName(categoryService.updateCategory(category) ? "/html/product/response_page"
				: "/html/product/error_message");
		return modelAndView;

	}

}
