package com.nrt.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.nrt.entity.Category;
import com.nrt.entity.SubCategory;
import com.nrt.service.SubCategoryService;

@Controller
public class SubCategoryController {

	@Autowired
	private SubCategoryService subCategoryService;

	@GetMapping("/subCategory")
	@PreAuthorize("hasRole('ADMIN') or hasRole('SUB_CATEGORY-ADD')")
	public ModelAndView subCategoryMethod(ModelAndView modelAndView) {
		modelAndView.setViewName("/html/product/add_subcategory");
		return modelAndView;
	}

	@PostMapping("/saveSubCategory")
	@PreAuthorize("hasRole('ADMIN') or hasRole('SUB_CATEGORY-ADD')")
	public ModelAndView saveCategoryMethod(@ModelAttribute SubCategory subCategory, ModelAndView modelAndView) {
		subCategoryService.saveSubCategory(subCategory);
		List<SubCategory> subCategories = subCategoryService.getAllSubCategory();
		modelAndView.addObject("subCategories", subCategories);
		modelAndView.setViewName("/html/product/list_subcategory");
		return modelAndView;
	}

	@GetMapping("/listCategory/{categoryId}")
	@PreAuthorize("hasRole('ADMIN') or hasRole('SUB_CATEGORY-LIST')")
	public ModelAndView listSubCategory(@PathVariable Long categoryId, ModelAndView modelAndView) {
		Category category = new Category();
		category.setCategoryId(categoryId);

		// Get subcategories by category
		List<SubCategory> subCategories = subCategoryService.getSubCategoriesByCategory(category);

		modelAndView.addObject("subCategories", subCategories);
		modelAndView.addObject("categoryId", categoryId);
		modelAndView.setViewName("/html/product/list_subcategory");
		return modelAndView;
	}

	@GetMapping("/listSubCategory")
	@PreAuthorize("hasRole('ADMIN') or hasRole('SUB_CATEGORY-LIST')")
	public ModelAndView listSubCategories(ModelAndView modelAndView) {
		List<SubCategory> subCategories = subCategoryService.getAllSubCategory();
		modelAndView.addObject("subCategories", subCategories);
		modelAndView.setViewName("/html/product/list_subcategory");
		return modelAndView;
	}

	@GetMapping("/deleteSubCategory/")
	@PreAuthorize("hasRole('ADMIN') or hasRole('SUB_CATEGORY-LIST')")
	public ModelAndView deleteSubCategory(@RequestParam("id") Long id, ModelAndView modelAndView) {
		subCategoryService.deleteSubCategory(id);
		List<SubCategory> subCategories = subCategoryService.getAllSubCategory();
		modelAndView.addObject("subCategories", subCategories);
		modelAndView.setViewName("/html/product/list_subcategory");
		return modelAndView;
	}

	@GetMapping("/updateSubCategoryById/")
	@PreAuthorize("hasRole('ADMIN') or hasRole('SUB_CATEGORY-UPDATE')")
	public ModelAndView updateSubCategory(@RequestParam("id") Long id, @ModelAttribute SubCategory subCategory,
			ModelAndView modelAndView) {
		subCategory = subCategoryService.getSubCategoryById(id);
		modelAndView.addObject("categories", subCategory);
		modelAndView.setViewName("/html/product/update_subcategory"); // View name without extension
		return modelAndView;

	}

	@PostMapping("/updateSubCategory")
	@PreAuthorize("hasRole('ADMIN') or hasRole('SUB_CATEGORY-UPDATE')")
	public ModelAndView updateSubCategory(@ModelAttribute SubCategory subCategory, ModelAndView modelAndView) {
		modelAndView.addObject("title", "Sub Category update");
		modelAndView.addObject("message", "Successfull");
		modelAndView.addObject("details", "\"Congratulations! Sub Category Update successfully !");
		modelAndView.addObject("error", "An error occurred while processing your request. Please try again later.");
		modelAndView.setViewName(subCategoryService.updateSubCategory(subCategory) ? "/html/product/response_page"
				: "/html/product/error_message");
		return modelAndView;

	}

}
