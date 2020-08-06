package com.example.dianping.handler;

import com.example.dianping.common.Result;
import com.example.dianping.common.ResultEnum;
import com.example.dianping.common.ResultVOUtil;
import com.example.dianping.exception.BussinessException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

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

    // 实现Form验证的统一异常处理
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    public Result ConstraintViolationExceptionHandler(ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream().map(ConstraintViolation::getMessage).collect(Collectors.joining(","));
        return ResultVOUtil.error(ResultEnum.PARAMETER_VALIDATION_ERROR.getCode(), message);
    }
}
