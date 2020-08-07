package com.example.dianping.controller.admin;

import com.example.dianping.common.AdminPermission;
import com.example.dianping.common.Result;
import com.example.dianping.common.ResultEnum;
import com.example.dianping.common.ResultVOUtil;
import com.example.dianping.exception.BussinessException;
import com.example.dianping.form.PageQuery;
import com.example.dianping.form.SellerCreateForm;
import com.example.dianping.model.SellerModel;
import com.example.dianping.service.SellerService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.List;

/**
 * @author Adward_Z
 * @date 2020/8/6
 */
@Controller
@RequestMapping("/admin/seller")
public class SellerController {

    @Autowired
    private SellerService sellerService;

    // 商户列表
    @GetMapping("/index")
    @AdminPermission
    public ModelAndView index(PageQuery pageQuery){

        // 分页
        PageHelper.startPage(pageQuery.getPage(), pageQuery.getSize());
        List<SellerModel> sellerModelList = sellerService.selectAll();
        PageInfo<SellerModel> pageInfo = new PageInfo<>(sellerModelList);

        ModelAndView modelAndView = new ModelAndView("/admin/seller/index");
        modelAndView.addObject("data", pageInfo);
        modelAndView.addObject("CONTROLLER_NAME", "seller");
        modelAndView.addObject("ACTION_NAME", "index");
        return modelAndView;
    }

    @GetMapping("/createpage")
    @AdminPermission
    public ModelAndView createPage(){
        ModelAndView modelAndView = new ModelAndView("/admin/seller/create");
        modelAndView.addObject("CONTROLLER_NAME", "seller");
        modelAndView.addObject("ACTION_NAME", "create");
        return modelAndView;
    }

    @PostMapping("/create")
    @AdminPermission
    public String create(@Valid SellerCreateForm sellerCreateForm, BindingResult result){
        if (result.hasErrors()){
            throw new BussinessException(ResultEnum.PARAMETER_VALIDATION_ERROR.getCode(), result.getFieldError().getDefaultMessage());
        }
        SellerModel sellerModel = new SellerModel();
        sellerModel.setName(sellerCreateForm.getName());
        sellerService.create(sellerModel);
        return "redirect:/admin/seller/index";
    }

    // 启用禁用使用ajax来实现，避免跳转网页了
    @PostMapping("/down")
    @AdminPermission
    @ResponseBody
    public Result down(@RequestParam("id") Integer id){
        SellerModel sellerModel = sellerService.changeStatus(id, 1);
        return ResultVOUtil.success(sellerModel);
    }

    // 启用禁用使用ajax来实现，避免跳转网页了
    @PostMapping("/up")
    @AdminPermission
    @ResponseBody
    public Result up(@RequestParam("id") Integer id){
        SellerModel sellerModel = sellerService.changeStatus(id, 0);
        return ResultVOUtil.success(sellerModel);
    }
}
