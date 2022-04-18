package com.epoch.loan.workshop.api.aspect;


import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.epoch.loan.workshop.common.constant.DynamicRequest;
import com.epoch.loan.workshop.common.constant.Field;
import com.epoch.loan.workshop.common.constant.ResultEnum;
import com.epoch.loan.workshop.common.mq.log.params.AccessLogParams;
import com.epoch.loan.workshop.common.params.params.result.Result;
import com.epoch.loan.workshop.common.util.LogUtil;
import com.epoch.loan.workshop.common.util.ThrowableUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.api.aspect
 * @className : FirstAspect
 * @createTime : 2021/3/10 21:59
 * @description : 请求首次切面，收集请求参数响应结果，生成日志
 */
@Aspect
@Component
@Order(3)
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
        // 获取request和response对象
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = Objects.requireNonNull(attributes).getRequest();
        HttpServletResponse response = Objects.requireNonNull(attributes).getResponse();

        // 获取访问日志对象
        AccessLogParams accessLogParams = (AccessLogParams) request.getAttribute(Field.ACCESS_LOG);

        // 结果集
        Result result = new Result();

        try {
            // 获取映射地址
            String mappingUrl = (String) request.getAttribute(Field.MAPPING_URL);

            // 响应流水号
            String serialNo = accessLogParams.getSerialNo();

            // Controller所需参数封装对象
            Object[] args = null;

            // 实例化请求参数JSON对象，防止请求参数为空导致JSON对象为NULL无法封装别的参数
            JSONObject jsonParams = new JSONObject();

            // 判断请求类型 拦截器中配置了只过滤 POST或GET 请求
            if (request.getMethod().equals(HttpMethod.POST.name())) {
                // POST类型请求
                // 读取请求参数
                jsonParams = readPostAsChars(request);
            } else if (request.getMethod().equals(HttpMethod.GET.name())) {
                // GET类型请求
                // 读取请求参数
                jsonParams = readGetAsChars(request);
            }

            // 将解析器
            accessLogParams.setRequest(jsonParams);

            // 将请求参数进行格式化(动态参数)
            jsonParams = formatDynamicRequestParams(jsonParams, mappingUrl);

            // 设置请求头参数
            injectionHeaderParams(request, jsonParams);

            // 转为String格式
            String jsonParamsStr = jsonParams.toJSONString();

            // 装载参数
            args = loadArgs(point, request, response, jsonParamsStr);

            // 调用Controller,传入封装好的请求参数
            Object objectResult = point.proceed(args);

            // 判断返回结果类型
            if (objectResult instanceof Result) {
                result = (Result) objectResult;
                result.setSerialNo(serialNo);

                // 将响应参数进行格式化(动态参数)
                result = formatDynamicResponseParams(result, mappingUrl);

                // 将返回前端的参数封装进日志
                accessLogParams.setResponse(result);
                accessLogParams.setEx(result.getEx());
                request.setAttribute(Field.ACCESS_LOG, accessLogParams);
                result.setEx("");
                return result;
            } else {
                // 将返回前端的参数封装进日志
                accessLogParams.setResponse(objectResult);
                request.setAttribute(Field.ACCESS_LOG, accessLogParams);
                return objectResult;
            }
        } catch (Exception e) {
            LogUtil.sysError("[FirstAspect around]", e);

            // 增加日志
            accessLogParams.setEx(ThrowableUtils.throwableToString(e));
            request.setAttribute(Field.ACCESS_LOG, accessLogParams);

            // 返回异常结果集
            result.setReturnCode(ResultEnum.SYSTEM_ERROR.code());
            result.setMessage(ResultEnum.SYSTEM_ERROR.message());
            return result;
        }
    }

    /**
     * 设置请求头公用参数
     *
     * @param request    请求对象
     * @param jsonParams 转换的Json参数
     */
    private void injectionHeaderParams(HttpServletRequest request, JSONObject jsonParams) {

        // 获取访问日志对象
        AccessLogParams accessLogParams = (AccessLogParams) request.getAttribute(Field.ACCESS_LOG);

        // 响应流水号
        String serialNo = accessLogParams.getSerialNo();
        if (StringUtils.isNotEmpty(serialNo)) {
            jsonParams.put(Field.SERIAL_NO, serialNo);
        }

        // Token标识
        String token = request.getHeader(Field.TOKEN);
        if (StringUtils.isNotEmpty(token)) {
            jsonParams.put(Field.TOKEN, token);
        }

        // App版本
        String appVersion = request.getHeader(Field.APP_VERSION);
        if (StringUtils.isNotEmpty(appVersion)) {
            jsonParams.put(Field.APP_VERSION, appVersion);
        }

        // App名称
        String appName = request.getHeader(Field.APP_NAME);
        if (StringUtils.isNotEmpty(appName)) {
            jsonParams.put(Field.APP_NAME, appName);
        }

        // 设备类型
        String mobileType = request.getHeader(Field.MOBILE_TYPE);
        if (StringUtils.isNotEmpty(mobileType)) {
            jsonParams.put(Field.MOBILE_TYPE, mobileType);
        }

        // 渠道标识
        String channelCode = request.getHeader(Field.CHANNEL_CODE);
        if (StringUtils.isNotEmpty(channelCode)) {
            jsonParams.put(Field.CHANNEL_CODE, channelCode);
        }


    }

    /**
     * 格式化动态请求参数
     *
     * @param jsonParams
     * @param mappingUrl
     * @return
     */
    private JSONObject formatDynamicRequestParams(JSONObject jsonParams, String mappingUrl) {
        // 判断映射地址是否为空
        if (StringUtils.isEmpty(mappingUrl)) {
            return jsonParams;
        }

        // 映射参数
        Map<String, String> mappingParams = DynamicRequest.REQUEST_MAPPING_MAPPING_CACHE.get(mappingUrl);

        // 判断映射配置是否为空
        if (ObjectUtils.isEmpty(mappingParams)) {
            return jsonParams;
        }

        // 转为String
        String jsonParamsStr = jsonParams.toJSONString();

        // 替换参数Key
        for (Map.Entry<String, String> params : mappingParams.entrySet()) {
            jsonParamsStr = jsonParamsStr.replace("\"" + params.getKey() + "\"", "\"" + params.getValue() + "\"");
        }

        return JSONObject.parseObject(jsonParamsStr);
    }

    /**
     * 格式化动态响应参数
     *
     * @param result
     * @param mappingUrl
     * @return
     */
    private Result formatDynamicResponseParams(Result result, String mappingUrl) {
        // 判断映射地址是否为空
        if (ObjectUtils.isEmpty(result)) {
            return result;
        }

        // 映射参数
        Map<String, String> mappingParams = DynamicRequest.RESPONSE_MAPPING_MAPPING_CACHE.get(mappingUrl);

        // 判断映射配置是否为空
        if (ObjectUtils.isEmpty(mappingParams)) {
            return result;
        }

        // 转为String
        String jsonResultStr = JSONObject.toJSONString(result);

        // 替换参数Key
        for (Map.Entry<String, String> params : mappingParams.entrySet()) {
            jsonResultStr = jsonResultStr.replace("\"" + params.getKey() + "\"", "\"" + params.getValue() + "\"");
        }

        // 只混淆data
        result = JSONObject.parseObject(jsonResultStr, Result.class);
        Object data = result.getData();

        // data数据类型判断
        if (ObjectUtils.isNotEmpty(data)) {
            // 不是基本数据类型及包装类
            boolean isPrimitive = data.getClass().isPrimitive();

            // 不是字符串
            boolean isString = data instanceof String;

            // 不是集合
            boolean isCollection = data instanceof Collection;

            // 则可以添加混淆参数
            boolean isJson = !isPrimitive && !isString && !isCollection;

            // 添加混淆参数
            if (isJson){
                List<String> list = DynamicRequest.RESPONSE_MAPPING_VIRTUAL_PARAMS_CACHE.get(mappingUrl);
                if (CollectionUtil.isNotEmpty(list)) {
                    JSONObject jsonData = (JSONObject) JSONObject.toJSON(data);
                    for (String virtual : list) {
                        jsonData.put(virtual, getRandomStr());
                    }
                    result.setData(jsonData);
                    return result;
                }
            }
        } else {
            List<String> list = DynamicRequest.RESPONSE_MAPPING_VIRTUAL_PARAMS_CACHE.get(mappingUrl);
            if (CollectionUtil.isNotEmpty(list)) {
                JSONObject jsonData = new JSONObject();
                for (String virtual : list) {
                    jsonData.put(virtual, getRandomStr());
                }
                result.setData(jsonData);
                return result;
            }
        }

        return JSONObject.parseObject(jsonResultStr, Result.class);
    }


    /**
     * 生成随机字符串 长度5-10
     *
     * @return
     */
    private String getRandomStr(){
        String res = "";

        // 65～90为26个大写英文字母，97～122号为26个小写英文字母
        Random random = new Random();

        // 长度
        int length = random.nextInt(10) + 5;

        for (int i = 0; i < length; i++) {
            char c;
            if (i % 2 == 0){
                c = (char) (random.nextInt(25 ) + 65);
            }else{
                c = (char) (random.nextInt(25 ) + 97);
            }
            res += c;
        }
        return res;
    }

    /**
     * 向Controller装载参数
     *
     * @param point
     * @param request
     * @param response
     * @param params
     * @return
     */
    private Object[] loadArgs(ProceedingJoinPoint point, HttpServletRequest request, HttpServletResponse response, String params) {
        // 获取Controller方法入参
        Object[] args = point.getArgs();

        // 循环装载参数
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof HttpServletRequest) {
                args[i] = request;
                continue;
            }
            if (args[i] instanceof HttpServletResponse) {
                args[i] = response;
                continue;
            }

            args[i] = JSON.parseObject(params, args[i].getClass());
        }

        return args;
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
    public JSONObject readPostAsChars(HttpServletRequest request) {
        // 请求参数
        String requestParams = null;

        // 获取请求参数
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder("");
        try {
            br = request.getReader();
            String str;
            while ((str = br.readLine()) != null) {
                sb.append(str);
            }
            requestParams = sb.toString();
            br.close();
        } catch (Exception e) {
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

        // 判断请求参数是否为空，如果为空尝试读取From提交方式
        if (StringUtils.isEmpty(requestParams)) {
            requestParams = getFormParamsAsJson(request);
        }

        // 判断请求参数是否为空
        if (StringUtils.isEmpty(requestParams)) {
            return new JSONObject();
        }

        // 判断请求参数是否是Json格式
        if (!isjson(requestParams)) {
            return new JSONObject();
        }

        return JSONObject.parseObject(requestParams);
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

