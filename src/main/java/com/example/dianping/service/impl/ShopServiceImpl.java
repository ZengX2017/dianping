package com.example.dianping.service.impl;

import com.example.dianping.common.ResultEnum;
import com.example.dianping.dao.ShopModelMapper;
import com.example.dianping.exception.BussinessException;
import com.example.dianping.model.CategoryModel;
import com.example.dianping.model.SellerModel;
import com.example.dianping.model.ShopModel;
import com.example.dianping.service.CategoryService;
import com.example.dianping.service.SellerService;
import com.example.dianping.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author Adward_Z
 * @date 2020/8/7
 */
@Service
public class ShopServiceImpl implements ShopService {

    @Autowired
    private ShopModelMapper mapper;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SellerService sellerService;

    @Override
    @Transactional
    public ShopModel create(ShopModel shopModel) {
        shopModel.setCreatedAt(new Date());
        shopModel.setUpdatedAt(new Date());

        // 校验商家是否存在正确
        SellerModel sellerModel = sellerService.get(shopModel.getSellerId());
        if (sellerModel == null){
            throw new BussinessException(ResultEnum.PARAMETER_VALIDATION_ERROR.getCode(), "商户不存在");
        }

        if (sellerModel.getDisabledFlag().intValue() == 1){
            throw new BussinessException(ResultEnum.PARAMETER_VALIDATION_ERROR.getCode(), "商户已禁用");
        }

        // 校验类目是否存在正确
        CategoryModel categoryModel = categoryService.get(shopModel.getCategordId());
        if (categoryModel == null){
            throw new BussinessException(ResultEnum.PARAMETER_VALIDATION_ERROR.getCode(), "类目不存在");
        }

        mapper.insertSelective(shopModel);
        return get(shopModel.getId());
    }

    @Override
    public ShopModel get(Integer id) {
        ShopModel shopModel = mapper.selectByPrimaryKey(id);
        if (shopModel == null){
            return null;
        }
        shopModel.setSellerModel(sellerService.get(shopModel.getSellerId()));
        shopModel.setCategoryModel(categoryService.get(shopModel.getCategordId()));

        return shopModel;
    }

    @Override
    public List<ShopModel> selectAll() {
        List<ShopModel> shopModelList = mapper.selectAll();
        shopModelList.forEach(shopModel -> {
            shopModel.setSellerModel(sellerService.get(shopModel.getSellerId()));
            shopModel.setCategoryModel(categoryService.get(shopModel.getCategordId()));

        });

        return shopModelList;
    }

    @Override
    public Integer countAllShop() {
        return mapper.countAllShop();
    }

    @Override
    public List<ShopModel> recommend(BigDecimal longitude, BigDecimal latitude) {
        List<ShopModel> shopModelList = mapper.recommend(longitude, latitude);
        shopModelList.forEach(shopModel -> {
            shopModel.setSellerModel(sellerService.get(shopModel.getSellerId()));
            shopModel.setCategoryModel(categoryService.get(shopModel.getCategordId()));
        });
        return shopModelList;
    }
}
