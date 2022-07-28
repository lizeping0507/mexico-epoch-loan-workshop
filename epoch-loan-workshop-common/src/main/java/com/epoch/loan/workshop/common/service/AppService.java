package com.epoch.loan.workshop.common.service;

import com.epoch.loan.workshop.common.params.params.BaseParams;
import com.epoch.loan.workshop.common.params.params.result.AppKeysResult;
import com.epoch.loan.workshop.common.params.params.result.Result;

/**
 * @author Shangkunfeng
 * @packagename : com.epoch.loan.workshop.common.service
 * @className : AppService
 * @createTime : 2022/03/29 15:45
 * @Description: App相关业务
 */
public interface AppService {
    Result<AppKeysResult> getAppKeys(BaseParams params);
}
