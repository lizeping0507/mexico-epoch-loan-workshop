package com.epoch.loan.workshop.common.service;

import com.epoch.loan.workshop.common.params.params.BaseParams;
import com.epoch.loan.workshop.common.params.params.request.AddRemittanceAccountParams;
import com.epoch.loan.workshop.common.params.params.result.AddRemittanceAccountResult;
import com.epoch.loan.workshop.common.params.params.result.RemittanceAccountListResult;
import com.epoch.loan.workshop.common.params.params.result.RemittanceBankListResult;
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

    /**
     * 新增放款账户
     *
     * @param addRemittanceAccountParams
     * @return
     * @throws Exception
     */
    Result<AddRemittanceAccountResult> addRemittanceAccount(AddRemittanceAccountParams addRemittanceAccountParams) throws Exception;

    /**
     * 银行账户列表
     *
     * @param baseParams
     * @return
     */
    Result<RemittanceBankListResult> remittanceBankList(BaseParams baseParams);
}
