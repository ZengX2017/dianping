package com.example.dianping.common;

import org.springframework.validation.BindingResult;

/**
 * @author Adward_Z
 * @date 2020/7/22
 */
public class ResultVOUtil {
    public static Result success(Object object){
        Result resultVO = new Result();
        resultVO.setCode(200);
        resultVO.setData(object);
        resultVO.setMessage("成功");
        return resultVO;
    }

    public static Result success(){
        return success(null);
    }

    public static Result error(Integer code, String msg){
        Result resultVO = new Result();
        resultVO.setCode(code);
        resultVO.setMessage(msg);
        return resultVO;
    }

    public static Result error(ResultEnum resultEnum){
        Result resultVO = new Result();
        resultVO.setCode(resultEnum.getCode());
        resultVO.setMessage(resultEnum.getMessage());
        return resultVO;
    }

    public static Result error(Integer code, BindingResult bindingResult){
        Result resultVO = new Result();
        resultVO.setCode(code);
        resultVO.setMessage(bindingResult.getFieldError().getField() + " " + bindingResult.getFieldError().getDefaultMessage());
        return resultVO;
    }
}
