package com.example.dianping.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author Adward_Z
 * @date 2020/8/6
 */
@Data
public class LoginForm {
    @NotBlank(message = "手机号码不能为空")
    private String phone;

    @NotBlank(message = "密码不能为空")
    private String password;
}
