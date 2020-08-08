package com.example.dianping.service.impl;

import com.example.dianping.common.ResultEnum;
import com.example.dianping.dao.CategoryModelMapper;
import com.example.dianping.exception.BussinessException;
import com.example.dianping.model.CategoryModel;
import com.example.dianping.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author Adward_Z
 * @date 2020/8/7
 */
@Service
public class CategoryServiceImpl implements CategoryService{

    @Autowired
    private CategoryModelMapper categoryModelMapper;

    @Override
    @Transactional
    public CategoryModel create(CategoryModel categoryModel) {
        categoryModel.setCreatedAt(new Date());
        categoryModel.setUpdatedAt(new Date());
        // 数据库用name索引不唯一，所以要判断一下
        try{
            categoryModelMapper.insertSelective(categoryModel);
        }catch (DuplicateKeyException e){
            throw new BussinessException(ResultEnum.CATEGORT_NAME_DUPLICATED);
        }
        return get(categoryModel.getId());
    }

    @Override
    public CategoryModel get(Integer id) {
        return categoryModelMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<CategoryModel> selectAll() {
        return categoryModelMapper.selectAll();
    }

    @Override
    public Integer countAllCategory() {
        return categoryModelMapper.countAllCategory();
    }
}
