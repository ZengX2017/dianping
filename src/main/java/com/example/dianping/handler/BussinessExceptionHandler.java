package com.example.dianping.handler;

import com.example.dianping.common.Result;
import com.example.dianping.common.ResultVOUtil;
import com.example.dianping.exception.BussinessException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Adward_Z
 * @date 2020/7/22
 */
@ControllerAdvice
public class BussinessExceptionHandler {

    @ExceptionHandler(value = BussinessException.class)
    @ResponseBody
    public Result hanlderSellException(BussinessException e){
        return ResultVOUtil.error(e.getCode(), e.getMessage());
    }
}
