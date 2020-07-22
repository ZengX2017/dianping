package com.example.dianping.controller;

import com.example.dianping.common.Result;
import com.example.dianping.common.ResultEnum;
import com.example.dianping.common.ResultVOUtil;
import com.example.dianping.exception.BussinessException;
import com.example.dianping.form.RegisterForm;
import com.example.dianping.model.UserModel;
import com.example.dianping.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

/**
 * @author Adward_Z
 * @date 2020/7/22
 */
@Validated
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/index")
    public ModelAndView index(){
        String userName = "imooc";
        ModelAndView modelAndView = new ModelAndView("/test.html");
        modelAndView.addObject("name", userName);
        return modelAndView;
    }

    @GetMapping("/get")
    @ResponseBody
    private Result getUserById(@RequestParam("id") Integer id){
        UserModel userModel = userService.getUser(id);
        if (userModel == null){
            return ResultVOUtil.error(ResultEnum.NO_OBJECT_FOUND.getCode(), ResultEnum.NO_OBJECT_FOUND.getMessage());
        }
        return ResultVOUtil.success(userModel);
    }

    @PostMapping("/register")
    @ResponseBody
    public Result register(@RequestBody @Valid RegisterForm form, BindingResult result) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        if (result.hasErrors()){
            throw new BussinessException(ResultEnum.PARAMETER_VALIDATION_ERROR.getCode(), result.getFieldError().getDefaultMessage());
        }

        UserModel registerUser = new UserModel();
        registerUser.setPhone(form.getPhone());
        registerUser.setPassword(form.getPassword());
        registerUser.setNickName(form.getNickName());
        registerUser.setGender(form.getGender());

        UserModel register = userService.register(registerUser);

        return ResultVOUtil.success(register);
    }

}
