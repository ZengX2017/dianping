package com.example.dianping.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author Adward_Z
 * @date 2020/8/7
 */
@Data
public class SellerCreateForm {

    @NotBlank(message = "商户名不能为空")
    private String name;
}
