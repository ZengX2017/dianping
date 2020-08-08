package com.example.dianping.controller.admin;

import com.example.dianping.common.AdminPermission;
import com.example.dianping.common.ResultEnum;
import com.example.dianping.exception.BussinessException;
import com.example.dianping.form.CategoryForm;
import com.example.dianping.form.PageQuery;
import com.example.dianping.model.CategoryModel;
import com.example.dianping.service.CategoryService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sun.org.apache.xpath.internal.SourceTree;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.List;

/**
 * @author Adward_Z
 * @date 2020/8/7
 */
@Controller("/admin/category")
@RequestMapping("/admin/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // 品类列表
    @GetMapping("/index")
    @AdminPermission
    public ModelAndView index(PageQuery pageQuery){

        // 分页
        PageHelper.startPage(pageQuery.getPage(), pageQuery.getSize());
        List<CategoryModel> categoryModelList = categoryService.selectAll();
        PageInfo<CategoryModel> pageInfo = new PageInfo<>(categoryModelList);

        ModelAndView modelAndView = new ModelAndView("/admin/category/index");
        modelAndView.addObject("data", pageInfo);
        modelAndView.addObject("CONTROLLER_NAME", "category");
        modelAndView.addObject("ACTION_NAME", "index");
        return modelAndView;
    }

    @GetMapping("/createpage")
    @AdminPermission
    public ModelAndView createPage(){
        ModelAndView modelAndView = new ModelAndView("/admin/category/create");
        modelAndView.addObject("CONTROLLER_NAME", "category");
        modelAndView.addObject("ACTION_NAME", "create");
        return modelAndView;
    }

    @PostMapping("/create")
    @AdminPermission
    public String create(@Valid CategoryForm categoryForm, BindingResult result){
        if (result.hasErrors()){
            throw new BussinessException(ResultEnum.PARAMETER_VALIDATION_ERROR.getCode(), result.getFieldError().getDefaultMessage());
        }
        CategoryModel categoryModel = new CategoryModel();

        /**
         * TODO: 使用BeanUtils能少些很多set语句
         * */
        BeanUtils.copyProperties(categoryForm, categoryModel);
//        categoryModel.setName(categoryForm.getName());
//        categoryModel.setIconUrl(categoryForm.getIconUrl());
//        categoryModel.setSort(categoryForm.getSort());
        categoryService.create(categoryModel);
        return "redirect:/admin/category/index";
    }
}
