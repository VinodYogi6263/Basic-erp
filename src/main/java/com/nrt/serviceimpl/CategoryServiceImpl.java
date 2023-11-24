package com.nrt.serviceimpl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nrt.entity.Category;
import com.nrt.exception.CantDeleteException;
import com.nrt.repository.CategoryRepository;
import com.nrt.request.CategoryRequest;
import com.nrt.service.CategoryService;

@Service
public class CategoryServiceImpl implements CategoryService {

	@Autowired
	private CategoryRepository categoryRepository;

	@Override
	public boolean saveCategory(CategoryRequest categoryRequest) {
		Category category = new Category();
		category.setCategoryId(categoryRequest.getCategoryId());
		category.setCategoryName(categoryRequest.getCategoryName());
		if (categoryRepository.existsById(category.getCategoryId())) {
			return false;
		} else {
			categoryRepository.save(category);
			return true;
		}
	}

	@Override
	public Category getCategoryById(Long id) {
		Optional<Category> findById = categoryRepository.findById(id);
		return findById.get();
	}

	@Override
	public void deleteCategory(Long id) {
		try {
			categoryRepository.deleteById(id);
		} catch (Exception e) {
			throw new CantDeleteException("Category  can not be deleted..! Cause> " + e.getMessage());
		}

	}

	@Override
	public boolean updateCategory(Category category) {
		try {
			Category updateCategory = categoryRepository.save(category);
			return updateCategory != null; // Return true if the save operation was successful
		} catch (Exception e) {
			e.printStackTrace(); // You can handle the exception as needed
			return false; // Return false if an exception occurs during the save operation
		}
	}

	@Override
	public List<Category> getAllCategory() {
		return categoryRepository.findAll();

	}

}
