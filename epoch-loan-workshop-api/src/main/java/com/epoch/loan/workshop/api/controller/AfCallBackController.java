package com.epoch.loan.workshop.api.controller;

import com.alibaba.fastjson.JSON;
import com.epoch.loan.workshop.common.config.URL;
import com.epoch.loan.workshop.common.constant.ResultEnum;
import com.epoch.loan.workshop.common.params.params.request.AfCallBackParams;
import com.epoch.loan.workshop.common.params.params.result.Result;
import com.epoch.loan.workshop.common.util.LogUtil;
import com.epoch.loan.workshop.common.util.ThrowableUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 魏玉强
 * @packagename : com.epoch.loan.workshop.api.controller
 * @className : AfCallBackController
 * @createTime : 2022/07/22 18:57
 * @Description:
 */
@RestController
@RequestMapping(URL.AF)
public class AfCallBackController extends BaseController {

    /**
     * af 回调业务处理表
     *
     * @param params af回调参数
     * @return
     */
    @PostMapping(URL.CALL_BACK)
    public Object afCallBack(@RequestBody AfCallBackParams params) {
        LogUtil.sysInfo("af 回传信息：{}", JSON.toJSONString(params));

        // 结果集
        Result<?> result = new Result<>();

        try {

            // af 回调业务处理
            return afCallBackService.afCallBack(params);
        } catch (Exception e) {
            LogUtil.sysError("[AfCallBackController afCallBack]", e);

            // 异常返回结果
            result.setEx(ThrowableUtils.throwableToString(e));
            result.setReturnCode(ResultEnum.SYSTEM_ERROR.code());
            result.setMessage(ResultEnum.SYSTEM_ERROR.message());
            return result;
        }
    }
}
