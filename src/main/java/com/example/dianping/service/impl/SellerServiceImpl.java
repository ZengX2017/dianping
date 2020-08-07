package com.example.dianping.service.impl;

import com.example.dianping.common.ResultEnum;
import com.example.dianping.dao.SellerModelMapper;
import com.example.dianping.exception.BussinessException;
import com.example.dianping.model.SellerModel;
import com.example.dianping.service.SellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author Adward_Z
 * @date 2020/8/6
 */
@Service
public class SellerServiceImpl implements SellerService {

    @Autowired
    private SellerModelMapper sellerModelMapper;

    @Override
    @Transactional
    public SellerModel create(SellerModel sellerModel) {
        sellerModel.setCreatedAt(new Date());
        sellerModel.setUpdatedAt(new Date());
        sellerModel.setRemarkScore(new BigDecimal(0));
        sellerModel.setDisabledFlag(0);
        sellerModelMapper.insertSelective(sellerModel);
        return get(sellerModel.getId());
    }

    @Override
    public SellerModel get(Integer id) {
        return sellerModelMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<SellerModel> selectAll() {
        return sellerModelMapper.selectAll();
    }

    @Override
    public SellerModel changeStatus(Integer id, Integer disableFlag) {
        SellerModel sellerModel = get(id);
        if (sellerModel == null){
            throw new BussinessException(ResultEnum.PARAMETER_VALIDATION_ERROR);
        }
        sellerModel.setDisabledFlag(disableFlag);
        sellerModelMapper.updateByPrimaryKeySelective(sellerModel);
        return sellerModel;
    }
}
