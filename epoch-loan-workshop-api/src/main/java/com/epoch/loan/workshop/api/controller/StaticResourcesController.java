package com.epoch.loan.workshop.api.controller;

import com.epoch.loan.workshop.common.config.URL;
import com.epoch.loan.workshop.common.params.params.request.StaticResourcesParam;
import com.epoch.loan.workshop.common.util.LogUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.api.controller.forward;
 * @className : StaticResourcesController
 * @createTime : 2022/3/18 11:08
 * @description : 静态资源接口跳转
 */
@Controller
@RequestMapping(URL.H5)
public class StaticResourcesController extends BaseController {

    /**
     * 注册协议地址
     *
     * @param params param
     * @return 重定向
     */
    @GetMapping(URL.CONTRACT)
    public String contractPage(StaticResourcesParam params, HttpServletResponse response) throws IOException {
        // 静态资源url
        String staticUrl = staticResourcesService.contractPage(params);

        // 重定向 到静态页面
        response.sendRedirect(staticUrl);
        return null;
    }

    /**
     * 注册协议地址
     *
     * @param param param
     * @return 重定向
     */
    @GetMapping(URL.REGISTER)
    public String registerPage(StaticResourcesParam param, HttpServletResponse response) throws IOException {
        // 静态资源url
        String staticUrl = staticResourcesService.getRegisterPageUrl(param.getAppName());

        // 重定向 到静态页面
        response.sendRedirect(staticUrl);
        return null;
    }

    /**
     * 帮助协议接口
     *
     * @param param param
     * @return 重定向
     */
    @GetMapping(URL.HELP)
    public String helpPage(StaticResourcesParam param, HttpServletResponse response) throws IOException {
        // 静态资源url
        String staticUrl = staticResourcesService.getHelpPageUrl(param.getAppName());

        // 重定向 到静态页面
        response.sendRedirect(staticUrl);
        return null;
    }

    /**
     * 隐私协议接口
     *
     * @param param param
     * @return 重定向
     */
    @GetMapping(URL.PRIVACY)
    public String privacyPage(StaticResourcesParam param, HttpServletResponse response) throws IOException {
        // 静态资源url
        String staticUrl = staticResourcesService.getPrivacyPageUrl(param.getAppName());

        // 重定向 到静态页面
        response.sendRedirect(staticUrl);
        return null;
    }

    /**
     * utr引导视频接口
     *
     * @param param param
     * @return 重定向
     */
    @GetMapping(URL.VIDEO_UTR)
    public String videoUtrPage(StaticResourcesParam param, HttpServletResponse response) throws IOException {
        // 静态资源url
        String staticUrl = staticResourcesService.getVideoUtrPageUrl(param.getAppName());

        // 重定向 到静态页面
        response.sendRedirect(staticUrl);
        return null;
    }

}
