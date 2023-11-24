package com.nrt.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nrt.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long>{

}
