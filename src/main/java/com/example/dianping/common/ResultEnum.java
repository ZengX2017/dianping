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
    // 通用类型错误1001开头
    NO_OBJECT_FOUND(1001, "对象不存在"),
    UNKNOWN_ERROR(1002, "未知错误"),
    NO_HANDLER_FOUND(1003, "找不到执行的路径操作"),
    BIND_EXCEPTION_ERROR(1004, "请求参数错误"),
    PARAMETER_VALIDATION_ERROR(1005, "请求参数校验失败"),

    // 用户服务相关的错误类型2001开头
    REGISTER_DUP_FAIL(2001, "用户已存在"),
    LOGIN_FAIL(2002, "手机号或者密码错误"),

    // admin相关的错误类型3001开头
    ADMIN_SHOULD_LOGIN(3001, "管理员需要先登录"),

    // 品类相关的错误类型4001开头DuplicateKeyException
    CATEGORT_NAME_DUPLICATED(4001, "品类名已存在"),
    ;

    private Integer code;

    private String message;

}
