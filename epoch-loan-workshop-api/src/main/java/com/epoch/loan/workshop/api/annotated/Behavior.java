package com.epoch.loan.workshop.api.annotated;

import java.lang.annotation.*;

/**
 * @author : Duke
 * @packageName : com.epoch.pf.option.api.annotated
 * @className : Behavior
 * @createTime : 2021/3/10 21:59
 * @description : 操作行为注解
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Behavior {

    /**
     * 行为描述
     *
     * @return
     */
    String desc() default "无记录行为";
}
