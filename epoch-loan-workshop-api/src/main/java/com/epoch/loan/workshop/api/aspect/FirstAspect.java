package com.epoch.loan.workshop.api.aspect;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.epoch.loan.workshop.common.constant.Field;
import com.epoch.loan.workshop.common.constant.ReturnCodeField;
import com.epoch.loan.workshop.common.constant.ReturnMessage;
import com.epoch.loan.workshop.common.params.result.Result;
import com.epoch.loan.workshop.common.params.system.AccessLogParams;
import com.epoch.loan.workshop.common.util.LogUtil;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Objects;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.api.aspect
 * @className : FirstAspect
 * @createTime : 2021/3/10 21:59
 * @description : 请求首次切面，收集请求参数响应结果，生成日志
 */
@Aspect
@Component
@Order(2)
public class FirstAspect {

    /**
     * 拦截地址
     */
    @Pointcut("execution(* com.epoch.loan.workshop.api.controller.*Controller.*(..))")
    private void firstAspect() {
    }

    /**
     * 方法开始执行
     * 将请求参数初步的校验及封装（JSON格式校验，请求流水号封装）
     * 将controller返回参数进行了统一的格式封装
     *
     * @param point 切面函数
     * @return
     * @throws Throwable
     */
    @Around("firstAspect()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        // 结果集
        Result result = new Result();

        try {
            // 获取request对象
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = Objects.requireNonNull(attributes).getRequest();

            // 获取访问日志对象
            AccessLogParams accessLogParams = (AccessLogParams) request.getAttribute(Field.ACCESS_LOG);

            // Controller所需参数封装对象
            Object[] args = null;

            // 实例化请求参数JSON对象，防止请求参数为空导致JSON对象为NULL无法封装别的参数
            JSONObject jsonParams = new JSONObject();

            // 判断请求类型 拦截器中配置了只过滤 POST或GET 请求
            if (request.getMethod().equals(HttpMethod.POST.name())) {
                // POST类型请求
                // 读取请求参数
                String params = readPostAsChars(request);

                // 判断请求参数是否是JSON类型数据
                if (!isjson(params) || StringUtils.isEmpty(params)) {
                    // 如果不是JSON 尝试将Form数据转换为Json
                    String formParamsAsJson = getFormParamsAsJson(request);
                    if (isjson(formParamsAsJson)) {
                        params = formParamsAsJson;
                    } else {
                        // 如果仍然不是Json 返回异常结果集
                        result.setReturnCode(ReturnCodeField.PARAMS_FAIL);
                        result.setMessage(ReturnMessage.NOT_JSON);
                        return result;
                    }
                }

                // 请求参数不为空，将请求参数转为JSON对象类型
                if (StringUtils.isNotEmpty(params)) {
                    jsonParams = JSONObject.parseObject(params);
                } else {
                    jsonParams = new JSONObject();
                }

                // 将请求参数转为JSONObject类型，并封装请求流水号
                jsonParams.put(Field.SERIAL_NO, accessLogParams.getSerialNo());

                // 将请求参数封装进日志
                if (StringUtils.isNotEmpty(jsonParams.toJSONString())) {
                    accessLogParams.setRequest(jsonParams);
                }

                // 获取Controller所需参数类型，将JSON转换成对应的类型(for 可以同步修改参数)
                args = point.getArgs();
                for (int i = 0; i < args.length; i++) {
                    if (args[i] instanceof HttpServletRequest) {
                        args[i] = request;
                        continue;
                    }
                    args[i] = JSON.parseObject(jsonParams.toJSONString(), args[i].getClass());
                }
            } else if (request.getMethod().equals(HttpMethod.GET.name())) {
                // GET类型请求
                // 读取请求参数
                jsonParams = readGetAsChars(request);

                // 将请求参数转为JSONObject类型，并封装请求流水号
                jsonParams.put(Field.SERIAL_NO, accessLogParams.getSerialNo());

                // 将请求参数封装进日志
                if (StringUtils.isNotEmpty(jsonParams.toJSONString())) {
                    accessLogParams.setRequest(jsonParams);
                }

                // 获取Controller所需参数类型，将JSON转换成对应的类型(for 可以同步修改参数)
                args = point.getArgs();
                for (int i = 0; i < args.length; i++) {
                    if (args[i] instanceof HttpServletRequest) {
                        args[i] = request;
                        continue;
                    }
                    args[i] = JSON.parseObject(jsonParams.toJSONString(), args[i].getClass());
                }
            }
            // 调用Controller,传入封装好的请求参数
            Object objectResult = point.proceed(args);

            // 判断返回结果类型
            if (objectResult instanceof Result) {
                result = (Result) objectResult;
                result.setSerialNo(accessLogParams.getSerialNo());

                // 将返回前端的参数封装进日志
                accessLogParams.setResponse(result);
                accessLogParams.setEx(result.getEx());
                result.setEx("");
                request.setAttribute(Field.ACCESS_LOG, accessLogParams);
                return result;
            } else {
                // 将返回前端的参数封装进日志
                accessLogParams.setResponse(objectResult);
                request.setAttribute(Field.ACCESS_LOG, accessLogParams);
                return objectResult;
            }
        } catch (Exception e) {
            LogUtil.sysError("[FirstAspect around]", e);

            // 返回异常结果集
            result.setReturnCode(ReturnCodeField.SYSTEM_ERROR);
            result.setMessage(ReturnMessage.SYSTEM_ERROR);
            return result;
        }
    }

    /**
     * 读取GET请求参数
     *
     * @param request
     * @return
     */
    public JSONObject readGetAsChars(HttpServletRequest request) {
        JSONObject jsonObject = new JSONObject();
        Enumeration<?> enu = request.getParameterNames();
        while (enu.hasMoreElements()) {
            String paraName = (String) enu.nextElement();
            jsonObject.put(paraName, request.getParameter(paraName));
        }
        return jsonObject;
    }

    /**
     * 读取POST请求参数
     *
     * @param request 请求对象
     * @return
     */
    public String readPostAsChars(HttpServletRequest request) {
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder("");
        try {
            br = request.getReader();
            String str;
            while ((str = br.readLine()) != null) {
                sb.append(str);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != br) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

    /**
     * 判断是否是JSON数据
     *
     * @param string
     * @return
     */
    private boolean isjson(String string) {
        try {
            JSONObject jsonStr = JSONObject.parseObject(string);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 把formData数据读取成Json
     *
     * @param request
     * @return
     */
    private String getFormParamsAsJson(HttpServletRequest request) {
        JSONObject jsonObject = new JSONObject();
        Enumeration<?> enu = request.getParameterNames();
        while (enu.hasMoreElements()) {
            String paraName = (String) enu.nextElement();
            jsonObject.put(paraName, request.getParameter(paraName));
        }
        return jsonObject.toJSONString();
    }
}

