package com.epoch.loan.workshop.api.web;

import com.epoch.loan.workshop.common.constant.DynamicRequest;
import com.epoch.loan.workshop.common.constant.Field;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.api.web;
 * @className : ForwardFilter
 * @createTime : 2022/3/15 14:25
 * @description : 动态地址Filter
 */
@Data
public class ForwardFilter implements Filter {

    /**
     * 请求调用
     *
     * @param servletRequest
     * @param servletResponse
     * @param chain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws ServletException, IOException {
        // 获取Request对象和Response对象
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // 请求地址
        String requestUrl = request.getRequestURI();

        // 获真实地址
        String url = DynamicRequest.URL_MAPPING_CACHE.get(requestUrl);

        // 判断是否有对应的真实地址
        if (StringUtils.isEmpty(url)) {
            // 未命中 , 正常请求
            chain.doFilter(servletRequest,servletResponse);
            return;
        }

        // 跳转真实地址
        request.setAttribute(Field.MAPPING_URL, requestUrl);
        RequestDispatcher requestDispatcher = request.getRequestDispatcher(url);
        requestDispatcher.forward(request, response);
        return;
    }
}

