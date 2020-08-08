package com.example.dianping.controller;

import com.example.dianping.common.Result;
import com.example.dianping.common.ResultEnum;
import com.example.dianping.common.ResultVOUtil;
import com.example.dianping.exception.BussinessException;
import com.example.dianping.model.CategoryModel;
import com.example.dianping.model.ShopModel;
import com.example.dianping.service.CategoryService;
import com.example.dianping.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    // 搜索服务V1.0
    @PostMapping("/search")
    @ResponseBody
    public Result search(@RequestParam("longitude") BigDecimal longitude,
                         @RequestParam("latitude") BigDecimal latitude,
                         @RequestParam("keyword") String keyword,
                         @RequestParam(name = "orderBy",required = false) Integer orderBy,
                         @RequestParam(name = "categoryId",required = false) Integer categoryId,
                         @RequestParam(name = "tags",required = false) String tags){
        if (longitude == null || latitude == null || StringUtils.isEmpty(keyword)){
            throw new BussinessException(ResultEnum.PARAMETER_VALIDATION_ERROR.getCode(), "经度或者维度或者关键字不能为空");
        }

        List<ShopModel> shopModelList = shopService.search(longitude, latitude, keyword, orderBy, categoryId, tags);
        List<CategoryModel> categoryModelList = categoryService.selectAll();
        List<Map<String, Object>> tagsAggregation = shopService.searchGroupByTags(keyword, categoryId, tags);
        Map<String, Object> resMap = new HashMap<>();
        resMap.put("shop", shopModelList);
        resMap.put("category", categoryModelList);
        resMap.put("tags", tagsAggregation);
        return ResultVOUtil.success(resMap);
    }

}
