package com.example.dianping.controller;

import com.example.dianping.common.Result;
import com.example.dianping.model.UserModel;
import com.example.dianping.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @author Adward_Z
 * @date 2020/7/22
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/get")
    @ResponseBody
    private Result getUserById(@RequestParam("id") Integer id){
        return Result.getResult(userService.getUser(id));
    }

}
