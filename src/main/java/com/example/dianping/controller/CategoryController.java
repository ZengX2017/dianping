package com.example.dianping.controller;

import com.example.dianping.common.Result;
import com.example.dianping.common.ResultVOUtil;
import com.example.dianping.model.CategoryModel;
import com.example.dianping.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author Adward_Z
 * @date 2020/8/7
 */
@Controller
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService service;

    @GetMapping("/list")
    @ResponseBody
    public Result list(){
        List<CategoryModel> categoryModelList = service.selectAll();
        return ResultVOUtil.success(categoryModelList);
    }
}
