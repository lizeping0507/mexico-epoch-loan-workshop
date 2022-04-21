package com.epoch.loan.workshop.common.service;

import com.epoch.loan.workshop.common.params.params.BaseParams;
import com.epoch.loan.workshop.common.params.params.result.RemittanceAccountListResult;
import com.epoch.loan.workshop.common.params.params.result.Result;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.service
 * @className : RemittanceService
 * @createTime : 2022/4/21 14:51
 * @description : 放款
 */
public interface RemittanceService {

    /**
     * 放款账户列表
     *
     * @param baseParams
     * @return
     */
    Result<RemittanceAccountListResult> remittanceAccountList(BaseParams baseParams);
}
