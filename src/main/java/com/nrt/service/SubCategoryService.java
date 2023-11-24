package com.nrt.service;

import java.util.List;

import com.nrt.entity.Category;
import com.nrt.entity.SubCategory;

public interface SubCategoryService {

	public List<SubCategory> getSubCategoriesByCategory(Category category);

	public Object getAllSubCategories();

	public void saveSubCategory(SubCategory subCategory);

	public List<SubCategory> getAllSubCategory();

	public void deleteSubCategory(Long id);

	public SubCategory getSubCategoryById(Long id);

	public boolean updateSubCategory(SubCategory subCategory);

}
