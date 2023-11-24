package com.nrt.serviceimpl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nrt.entity.Category;
import com.nrt.entity.SubCategory;
import com.nrt.exception.CantDeleteException;
import com.nrt.repository.SubCategoryRepository;
import com.nrt.service.SubCategoryService;

@Service
public class SubCategoryServiceImpl implements SubCategoryService {

	@Autowired
	private SubCategoryRepository subCategoryRepository;

	@Override
	public List<SubCategory> getSubCategoriesByCategory(Category category) {
		return subCategoryRepository.findAll();
	}

	@Override
	public Object getAllSubCategories() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveSubCategory(SubCategory subCategory) {
		subCategoryRepository.save(subCategory);
	}

	@Override
	public List<SubCategory> getAllSubCategory() {
		return subCategoryRepository.findAll();
	}

	@Override
	public void deleteSubCategory(Long id) {
		try {
			subCategoryRepository.deleteById(id);
		} catch (Exception e) {
			throw new CantDeleteException("Sub category  can not be deleted..! Cause> " + e.getMessage());
		}

	}

	@Override
	public SubCategory getSubCategoryById(Long id) {
		Optional<SubCategory> findById = subCategoryRepository.findById(id);
		return findById.get();
	}

	@Override
	public boolean updateSubCategory(SubCategory subCategory) {
		try {
			SubCategory updateSubCategory = subCategoryRepository.save(subCategory);
			return updateSubCategory != null; // Return true if the save operation was successful
		} catch (Exception e) {
			e.printStackTrace(); // You can handle the exception as needed
			return false; // Return false if an exception occurs during the save operation
		}
	}

}
