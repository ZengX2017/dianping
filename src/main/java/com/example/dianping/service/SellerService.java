package com.example.dianping.service;

import com.example.dianping.model.SellerModel;

import java.util.List;

/**
 * @author Adward_Z
 * @date 2020/8/6
 */
public interface SellerService {

    SellerModel create(SellerModel sellerModel);

    SellerModel get(Integer id);

    List<SellerModel> selectAll();

    SellerModel changeStatus(Integer id, Integer disableFlag);

    Integer countAllSeller();
}
