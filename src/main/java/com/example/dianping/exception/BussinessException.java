package com.example.dianping.exception;

import com.example.dianping.common.ResultEnum;
import lombok.Getter;

/**
 * @author Adward_Z
 * @date 2020/7/22
 */
@Getter
public class BussinessException extends RuntimeException {

    private Integer code;


    public BussinessException(ResultEnum resultEnum){
        super(resultEnum.getMessage());
        this.code = resultEnum.getCode();
    }

    public BussinessException(Integer code, String message){
        super(message);
        this.code = code;
    }
}
