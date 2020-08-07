package com.example.dianping.controller;

import com.example.dianping.common.Result;
import com.example.dianping.common.ResultEnum;
import com.example.dianping.common.ResultVOUtil;
import com.example.dianping.exception.BussinessException;
import com.example.dianping.model.ShopModel;
import com.example.dianping.service.CategoryService;
import com.example.dianping.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Adward_Z
 * @date 2020/8/7
 */
@Controller("/shop")
@RequestMapping("/shop")
public class ShopController {

    @Autowired
    private ShopService shopService;

    @Autowired
    private CategoryService categoryService;

    // 推荐服务V1.0
    @GetMapping("/recommend")
    @ResponseBody
    public Result recommend(@RequestParam("longitude") BigDecimal longitude,
                            @RequestParam("latitude") BigDecimal latitude){
        if (longitude == null || latitude == null){
            throw new BussinessException(ResultEnum.PARAMETER_VALIDATION_ERROR.getCode(), "经度或者维度不能为空");
        }

        List<ShopModel> shopModelList = shopService.recommend(longitude, latitude);
        return ResultVOUtil.success(shopModelList);
    }

}
