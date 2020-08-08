package com.example.dianping.service;

import com.example.dianping.model.CategoryModel;

import java.util.List;

/**
 * @author Adward_Z
 * @date 2020/8/7
 */
public interface CategoryService {

    CategoryModel create(CategoryModel categoryModel);

    CategoryModel get(Integer id);

    List<CategoryModel> selectAll();

    Integer countAllCategory();
}
