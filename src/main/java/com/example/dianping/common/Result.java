package com.example.dianping.common;

import lombok.Data;

/**
 * @author Adward_Z
 * @date 2020/7/22
 */
@Data
public class Result<T> {

    private Integer code;

    private T data;

    private String message;

}
