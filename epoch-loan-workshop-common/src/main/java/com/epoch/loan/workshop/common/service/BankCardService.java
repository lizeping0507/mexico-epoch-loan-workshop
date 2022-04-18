package com.epoch.loan.workshop.common.service;

import com.epoch.loan.workshop.common.params.params.request.BankCardParams;
import com.epoch.loan.workshop.common.params.params.request.BindBankCardParams;
import com.epoch.loan.workshop.common.params.params.request.ParentParams;
import com.epoch.loan.workshop.common.params.params.result.*;

/**
 * @author 魏玉强
 * @packagename : com.epoch.loan.workshop.common.service
 * @className : BankCardService
 * @createTime : 2022/03/29 15:45
 * @Description: 银行卡信息
 */
public interface BankCardService {

    /**
     * 获取银行支行信息
     *
     * @param params 支行上级封装类
     * @return 子集支行集合
     * @throws Exception 请求异常
     */
    Result<ListResult> getBankBranch(ParentParams params) throws Exception;

    /**
     * 银行卡列表
     *
     * @param bankCardParams 入参
     * @return 银行卡集合
     * @throws Exception 请求异常
     */
    Result<BankCardResult> bankCardList(BankCardParams bankCardParams) throws Exception;

    /**
     * 绑新卡
     *
     * @param bankCardParams 入参
     * @return 是否绑卡成功
     * @throws Exception 请求异常
     */
    Result<BindBankCardResult> bindNewBankCard(BindBankCardParams bankCardParams) throws Exception;

    /**
     * 确认旧卡
     *
     * @param bankCardParams 入参
     * @return 是否绑卡成功
     * @throws Exception 请求异常
     */
    Result<BindBankCardResult> confirmOldBankCard(BindBankCardParams bankCardParams) throws Exception;

    /**
     * 确认旧卡并领款
     *
     * @param params 绑卡参数
     * @return 是否绑定成功
     * @throws Exception 请求异常
     */
    Result<BindBankCardResult> confirmOldBankCardAndLoanAmount(BindBankCardParams params) throws Exception;

    /**
     * 新增银行卡
     *
     * @param bindBankCardParams 银行卡参数
     * @return 银行卡信息
     * @throws Exception 请求异常
     */
    Result<AddBankcardResult> addBankcard(BindBankCardParams bindBankCardParams) throws Exception;
}
