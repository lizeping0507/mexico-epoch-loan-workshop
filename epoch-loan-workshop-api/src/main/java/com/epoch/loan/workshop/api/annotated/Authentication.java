package com.epoch.loan.workshop.api.annotated;

import java.lang.annotation.*;

/**
 * @author : Duke
 * @packageName : com.epoch.pf.option.api.annotated
 * @className : Authentication
 * @createTime : 2021/3/10 21:59
 * @description : 身份验证注解, 需要身份验证（登录）的请求需要加此注解来进行身份验证
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Authentication {
    /**
     * 权限
     *
     * @return
     */
    public boolean auth() default true;
}
