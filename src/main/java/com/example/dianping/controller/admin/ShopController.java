package com.example.dianping.controller.admin;

import com.example.dianping.common.AdminPermission;
import com.example.dianping.common.ResultEnum;
import com.example.dianping.exception.BussinessException;
import com.example.dianping.form.CategoryForm;
import com.example.dianping.form.PageQuery;
import com.example.dianping.form.ShopCreateForm;
import com.example.dianping.model.CategoryModel;
import com.example.dianping.model.ShopModel;
import com.example.dianping.service.CategoryService;
import com.example.dianping.service.ShopService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
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
@Controller("/admin/shop")
@RequestMapping("/admin/shop")
public class ShopController {

    @Autowired
    private ShopService shopService;

    // 门店列表
    @GetMapping("/index")
    @AdminPermission
    public ModelAndView index(PageQuery pageQuery){

        // 分页
        PageHelper.startPage(pageQuery.getPage(), pageQuery.getSize());
        List<ShopModel> shopModelList = shopService.selectAll();
        PageInfo<ShopModel> pageInfo = new PageInfo<>(shopModelList);

        ModelAndView modelAndView = new ModelAndView("/admin/shop/index");
        modelAndView.addObject("data", pageInfo);
        modelAndView.addObject("CONTROLLER_NAME", "shop");
        modelAndView.addObject("ACTION_NAME", "index");
        return modelAndView;
    }

    @GetMapping("/createpage")
    @AdminPermission
    public ModelAndView createPage(){
        ModelAndView modelAndView = new ModelAndView("/admin/shop/create");
        modelAndView.addObject("CONTROLLER_NAME", "shop");
        modelAndView.addObject("ACTION_NAME", "create");
        return modelAndView;
    }

    @PostMapping("/create")
    @AdminPermission
    public String create(@Valid ShopCreateForm shopCreateForm, BindingResult result){
        if (result.hasErrors()){
            throw new BussinessException(ResultEnum.PARAMETER_VALIDATION_ERROR.getCode(), result.getFieldError().getDefaultMessage());
        }
        ShopModel shopModel = new ShopModel();
        shopModel.setIconUrl(shopCreateForm.getIconUrl());
        shopModel.setAddress(shopCreateForm.getAddress());
        shopModel.setCategordId(shopCreateForm.getCategoryId());
        shopModel.setEndTime(shopCreateForm.getEndTime());
        shopModel.setStartTime(shopCreateForm.getStartTime());
        shopModel.setLongitude(shopCreateForm.getLongitude());
        shopModel.setLatitude(shopCreateForm.getLatitude());
        shopModel.setName(shopCreateForm.getName());
        shopModel.setPricePerMan(shopCreateForm.getPricePerMan());
        shopModel.setSellerId(shopCreateForm.getSellerId());

        shopService.create(shopModel);

        return "redirect:/admin/shop/index";
    }
}