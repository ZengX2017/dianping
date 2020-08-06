package com.example.dianping.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Adward_Z
 * @date 2020/7/22
 */
@Getter
@AllArgsConstructor
public enum ResultEnum {
    // 通用类型错误100开头
    NO_OBJECT_FOUND(101, "对象不存在"),
    UNKNOWN_ERROR(102, "未知错误"),
    NO_HANDLER_FOUND(103, "找不到执行的路径操作"),
    BIND_EXCEPTION_ERROR(104, "请求参数错误"),
    PARAMETER_VALIDATION_ERROR(105, "请求参数校验失败"),


    // 用户服务相关的错误类型300开头
    REGISTER_DUP_FAIL(301, "用户已存在"),
    LOGIN_FAIL(302, "手机号或者密码错误"),
    ;

    private Integer code;

    private String message;

}
