package com.epoch.loan.workshop.control.service;

import com.epoch.loan.workshop.common.constant.ResultEnum;
import com.epoch.loan.workshop.common.entity.mysql.LoanAppKeysEntity;
import com.epoch.loan.workshop.common.params.params.BaseParams;
import com.epoch.loan.workshop.common.params.params.result.AppKeysResult;
import com.epoch.loan.workshop.common.params.params.result.Result;
import com.epoch.loan.workshop.common.service.AppService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.control.service;
 * @className : AppServiceImpl
 * @createTime : 2022/4/1 17:44
 * @description : TODO
 */
@DubboService(timeout = 3000, cluster = "failfast", retries = 0)
public class AppServiceImpl extends BaseService implements AppService {
    /**
     * 获取App相关配置key
     *
     * @param params 通用参数
     * @return 结果
     */
    @Override
    public Result<AppKeysResult> getAppKeys(BaseParams params) {
        // 结果集
        Result<AppKeysResult> result = new Result<>();

        // 参数校验
        if (ObjectUtils.isEmpty(params) || StringUtils.isEmpty(params.getAppName())) {
            result.setReturnCode(ResultEnum.PARAM_ERROR.code());
            result.setMessage(ResultEnum.PARAM_ERROR.message());
            return result;
        }

        // 查询该包配置key
        String appName = params.getAppName();
        LoanAppKeysEntity loanAppKey = loanAppKeysDao.getLoanAppKeyByAppName(appName);

        // 判空
        if (ObjectUtils.isEmpty(loanAppKey)) {
            result.setReturnCode(ResultEnum.NO_EXITS.code());
            result.setMessage(ResultEnum.NO_EXITS.message());
            return result;
        }

        // 转换、封装
        AppKeysResult data = new AppKeysResult();
        BeanUtils.copyProperties(loanAppKey, data);
        result.setReturnCode(ResultEnum.SUCCESS.code());
        result.setMessage(ResultEnum.SUCCESS.message());
        result.setData(data);
        return result;
    }
}
