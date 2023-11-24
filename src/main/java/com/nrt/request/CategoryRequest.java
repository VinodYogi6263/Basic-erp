package com.nrt.request;

import java.util.List;

import com.nrt.entity.SubCategory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CategoryRequest {

	private long categoryId;
	private String categoryName;
	private List<SubCategory> subcategories;
}
