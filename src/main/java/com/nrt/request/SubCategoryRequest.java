package com.nrt.request;

import com.nrt.entity.Category;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SubCategoryRequest {

	private Long subCatagoryId;

	private String subCatagoryName;

	private String subCatagoryDescription;

	private Category category;

}
