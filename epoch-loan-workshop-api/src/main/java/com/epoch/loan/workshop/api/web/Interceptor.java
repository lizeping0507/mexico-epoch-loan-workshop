package com.epoch.loan.workshop.api.web;

import com.alibaba.fastjson.JSONObject;
import com.epoch.loan.workshop.common.constant.Field;
import com.epoch.loan.workshop.common.constant.ResultEnum;
import com.epoch.loan.workshop.common.mq.log.params.AccessLogParams;
import com.epoch.loan.workshop.common.params.params.result.Result;
import com.epoch.loan.workshop.common.util.IpUtil;
import com.epoch.loan.workshop.common.util.LogUtil;
import com.epoch.loan.workshop.common.util.ObjectIdUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author : Duke
 * @packageName : com.epoch.pf.option.api.web
 * @className : Interceptor
 * @createTime : 2021/3/10 21:59
 * @description : 访问拦截器
 */
@Configuration
public class Interceptor implements HandlerInterceptor {

    /**
     * 当前服务运行IP
     */
    private static String SERVER_IP = IpUtil.getIp();

    /**
     * 项目名称
     */
    @Value("${spring.application.name}")
    private String NAME;

    /**
     * 项目端口
     */
    @Value("${server.port}")
    private String PORT;

    /**
     * 请求之前执行
     * <p>
     * 此拦截器拦截所有请求
     * 防止一些比较低级的恶意攻击
     * 初始化日志
     * 记录请求开始时间及请求结束时间（拦截器中计算请求时间比Controller准确）
     *
     * @param request  请求对象
     * @param response 响应对象
     * @param handler  控制器
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 获取客户端IP
        String ip = IpUtil.getIPAddress(request);

        // 唯一流水号
        String serialNo = ObjectIdUtil.getObjectId();

        // 映射地址
        String mappingUrl = (String) request.getAttribute(Field.MAPPING_URL);

        // 访问日志
        AccessLogParams accessLogParams = new AccessLogParams();

        // 请求时间(yyyyMMddHHmmssSSS)
        accessLogParams.setRequestTime(System.currentTimeMillis());

        // 唯一流水号
        accessLogParams.setSerialNo(serialNo);

        // 请求地址
        accessLogParams.setUrl(request.getRequestURI());

        // 映射地址
        accessLogParams.setMappingUrl(mappingUrl);

        // 获取客户端IP
        accessLogParams.setIp(ip);

        // 当前服务器IP
        accessLogParams.setServerIp(SERVER_IP);

        // 当前服务端口
        accessLogParams.setPort(PORT);

        // 项目名称
        accessLogParams.setApplicationName(NAME);


        // 判断请求类型 只接受GET或POST类型请求
        if (!request.getMethod().equals(HttpMethod.POST.name()) && !request.getMethod().equals(HttpMethod.GET.name())) {
            // 不是规定请求类型返回请求类型异常
            // 向前端返回异常信息
            String result = viewWriter(response, serialNo, ResultEnum.METHOD_ERROR.code(), ResultEnum.METHOD_ERROR.message());

            // 将响应信息封装到日志中
            accessLogParams.setResponse(result);

            // 响应结束时间
            accessLogParams.setResponseTime(System.currentTimeMillis());

            // 打印请求日志
            LogUtil.request(accessLogParams);

            // 不向下执行
            return false;
        }

        // 访问日志
        request.setAttribute(Field.ACCESS_LOG, accessLogParams);

        // 基本校验成功
        return true;
    }

    /**
     * 向前端返回数据
     *
     * @param response   响应对象
     * @param serialNo   响应流水号
     * @param returnCode 返回码
     * @param message    响应信息
     * @return
     * @throws IOException
     */
    private String viewWriter(HttpServletResponse response, String serialNo, int returnCode, String message) throws IOException {
        // 返回结果
        Result result = new Result();
        result.setMessage(message);
        result.setReturnCode(returnCode);
        result.setSerialNo(serialNo);
        String jsonStr = JSONObject.toJSONString(result);

        /*
         * 返回体
         */
        response.setStatus(200);
        response.setContentType(Field.APPLICATION_JSON);
        response.setCharacterEncoding(Field.UTF_8);
        response.getWriter().write(jsonStr);
        response.getWriter().flush();
        response.getWriter().close();

        // 返回响应信息
        return jsonStr;
    }


    /**
     * 渲染之后执行
     * <p>
     * 记录请求结束时间，并且计算出本次请求耗时时间（只有选然后才算请求结束）
     *
     * @param request  请求对象
     * @param response 响应对象
     * @param handler  控制器
     * @param ex       异常
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

        // 从request中获取日志对象
        Object object = request.getAttribute(Field.ACCESS_LOG);

        // 判断日志是否为空
        if (ObjectUtils.isEmpty(object)) {
            return;
        }

        // 转换为访问日志对象
        AccessLogParams accessLogParams = (AccessLogParams) object;

        // 响应结束时间
        accessLogParams.setResponseTime(System.currentTimeMillis());

        // 计算请求耗时时间
        Long accessSpend = Long.valueOf(accessLogParams.getResponseTime()) - Long.valueOf(accessLogParams.getRequestTime());
        accessLogParams.setAccessSpend(accessSpend);

        // 打印请求日志
        LogUtil.request(accessLogParams);
    }
}
