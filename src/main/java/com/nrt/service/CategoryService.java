package com.nrt.service;

import java.util.List;

import com.nrt.entity.Category;
import com.nrt.request.CategoryRequest;

public interface CategoryService {

	public boolean saveCategory(CategoryRequest categoryRequest);

	public List<Category> getAllCategory();

	public Category getCategoryById(Long id);

	public void deleteCategory(Long id);

	public boolean updateCategory(Category category);

}
