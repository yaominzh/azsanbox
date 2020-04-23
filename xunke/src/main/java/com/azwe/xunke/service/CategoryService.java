package com.azwe.xunke.service;

import com.azwe.xunke.common.BusinessException;
import com.azwe.xunke.model.CategoryModel;

import java.util.List;

public interface CategoryService {

    CategoryModel create(CategoryModel categoryModel) throws BusinessException;
    CategoryModel get(Integer id);
    List<CategoryModel> selectAll();

    Integer countAllCategory();
}
