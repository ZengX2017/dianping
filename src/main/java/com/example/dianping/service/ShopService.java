package com.example.dianping.service;

import com.example.dianping.model.ShopModel;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

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

    List<ShopModel> search(BigDecimal longitude, BigDecimal latitude, String keyword, Integer orderBy, Integer categoryId, String tags);

    List<Map<String, Object>> searchGroupByTags(String keyword, Integer categoryId, String tags);

}
