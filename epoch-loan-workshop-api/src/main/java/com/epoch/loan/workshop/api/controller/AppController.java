package com.epoch.loan.workshop.api.controller;

import com.epoch.loan.workshop.common.config.URL;
import com.epoch.loan.workshop.common.constant.ResultEnum;
import com.epoch.loan.workshop.common.params.params.BaseParams;
import com.epoch.loan.workshop.common.params.params.result.AppKeysResult;
import com.epoch.loan.workshop.common.params.params.result.Result;
import com.epoch.loan.workshop.common.util.LogUtil;
import com.epoch.loan.workshop.common.util.ThrowableUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.api.controller
 * @className : SdkController
 * @createTime : 22/3/30 15:01
 * @description : APP相关
 */
@RestController
@RequestMapping(URL.APP)
public class AppController extends BaseController {

    /**
     * app端获取配置
     *
     * @return 结果
     */
    @PostMapping(URL.APP_KEYS)
    public Result<AppKeysResult> appKeys(BaseParams params) {
        // 结果集
        Result<AppKeysResult> result = new Result<>();

        try {
            return appService.getAppKeys(params);
        } catch (Exception e) {
            LogUtil.sysError("[AppController appKeys]", e);

            // 异常返回结果
            result.setEx(ThrowableUtils.throwableToString(e));
            result.setReturnCode(ResultEnum.SYSTEM_ERROR.code());
            result.setMessage(ResultEnum.SYSTEM_ERROR.message());
            return result;
        }

    }

}
