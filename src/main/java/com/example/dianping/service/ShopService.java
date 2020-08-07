package com.example.dianping.service;

import com.example.dianping.model.ShopModel;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Adward_Z
 * @date 2020/8/7
 */
public interface ShopService {

    ShopModel create(ShopModel shopModel);

    ShopModel get(Integer id);

    List<ShopModel> selectAll();

    Integer countAllShop();

    List<ShopModel> recommend(BigDecimal longitude, BigDecimal latitude);
}
