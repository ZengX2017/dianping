package com.example.dianping.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author Adward_Z
 * @date 2020/8/7
 */
@Data
public class CategoryForm {

    @NotBlank(message = "名称不能为空")
    private String name;

    @NotBlank(message = "iconUrl不能为空")
    private String iconUrl;

    @NotNull(message = "权重不能为空")
    private Integer sort;
}
