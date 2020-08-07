package com.example.dianping.common;

import com.example.dianping.controller.admin.AdminController;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * @author Adward_Z
 * @date 2020/8/6
 */
@Aspect
@Configuration //使其声明为一个Bean
public class ControllerAspect {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    // TODO 了解return null和joinPoint.proceed()的区别, PostMapping那段可能不要
    // 第一个*代表返回值，第二个*代表方法，第三个*代表所有参数的方法
    // &&后面控制admin中有@RequsetMapping（已用GetMapping和PostMapping分开表述）的方法
    @Around("execution(* com.example.dianping.controller.admin.*.*(..)) && (@annotation(org.springframework.web.bind.annotation.GetMapping) || @annotation(org.springframework.web.bind.annotation.PostMapping)) ")
    public Object adminControllerBeforeValidation(ProceedingJoinPoint joinPoint) throws Throwable {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        AdminPermission adminPermission = method.getAnnotation(AdminPermission.class);
        if (adminPermission == null){
            // 说明没有权限控制，放行
            Object proceed = joinPoint.proceed();
            return proceed;
        }
        // 判断当前管理员是否登录
        String email = (String) request.getSession().getAttribute(AdminController.CURRENT_ADMIN_SESSION);
        if (email == null){
            if (adminPermission.produceType().equals("text/html")){
                response.sendRedirect("/admin/admin/loginpage");
                return null;
            }else {
                return ResultVOUtil.error(ResultEnum.ADMIN_SHOULD_LOGIN);
            }
        }else {
            Object proceed = joinPoint.proceed();
            return proceed;
        }
    }
}
