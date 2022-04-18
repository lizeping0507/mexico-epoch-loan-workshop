package com.epoch.loan.workshop.api.controller;

import com.epoch.loan.workshop.common.config.URL;
import com.epoch.loan.workshop.common.constant.ResultEnum;
import com.epoch.loan.workshop.common.params.params.request.BankCardParams;
import com.epoch.loan.workshop.common.params.params.request.BindBankCardParams;
import com.epoch.loan.workshop.common.params.params.request.ParentParams;
import com.epoch.loan.workshop.common.params.params.result.*;
import com.epoch.loan.workshop.common.util.LogUtil;
import com.epoch.loan.workshop.common.util.ThrowableUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 魏玉强
 * @packagename : com.epoch.loan.workshop.api.controller
 * @className : BanklCardController
 * @createTime : 2022/03/29 14:24
 * @Description: 银行卡相关
 */
@RestController
@RequestMapping(URL.BANK_CARD)
public class BankCardController extends BaseController {

    /**
     * 获取银行支行信息
     *
     * @param params 支行上级封装类
     * @return 子集支行集合
     */
    @PostMapping(URL.BANK_BRANCH)
    public Result<ListResult> getBankBranch(ParentParams params) {
        // 结果集
        Result<ListResult> result = new Result<>();

        try {

            // 获取用户OCR认证提供商
            return bankCardService.getBankBranch(params);
        } catch (Exception e) {
            LogUtil.sysError("[BankCardController getBankBranch]", e);

            // 异常返回结果
            result.setEx(ThrowableUtils.throwableToString(e));
            result.setReturnCode(ResultEnum.SYSTEM_ERROR.code());
            result.setMessage(ResultEnum.SYSTEM_ERROR.message());
            return result;
        }
    }

    /**
     * 银行卡列表
     *
     * @param bankCardParams 入参
     * @return 银行卡集合
     */
    @PostMapping(URL.BANK_CARD_LIST)
    public Result<BankCardResult> bankCardList(BankCardParams bankCardParams){
        // 结果集
        Result<BankCardResult> result = new Result<>();

        try {
            // 银行卡列表
            return bankCardService.bankCardList(bankCardParams);
        }catch (Exception e){
            LogUtil.sysError("[BankCardController bankCardList]", e);

            // 异常返回结果
            result.setEx(ThrowableUtils.throwableToString(e));
            result.setReturnCode(ResultEnum.SYSTEM_ERROR.code());
            result.setMessage(ResultEnum.SYSTEM_ERROR.message());
            return result;
        }
    }

    /**
     * 新增银行卡 TODO 该接口目前不做新增银行卡操作，目前作为确认旧卡操作
     *
     * @param bindBankCardParams 银行卡信息
     * @return 银行卡信息
     */
    @PostMapping(URL.BIND_ADD_BANK_CARD)
    public Result<AddBankcardResult> addBankcard(BindBankCardParams bindBankCardParams){
        // 结果集
        Result<AddBankcardResult> result = new Result<>();

        try {
            // 银行卡列表
            return bankCardService.addBankcard(bindBankCardParams);
        }catch (Exception e){
            LogUtil.sysError("[BankCardController bindNewBankCard]", e);

            // 异常返回结果
            result.setEx(ThrowableUtils.throwableToString(e));
            result.setReturnCode(ResultEnum.SYSTEM_ERROR.code());
            result.setMessage(ResultEnum.SYSTEM_ERROR.message());
            return result;
        }
    }

    /**
     * 绑新卡
     *
     * @param bindBankCardParams 入参
     * @return 是否绑卡成功
     */
    @PostMapping(URL.BIND_NEW_BANK_CARD)
    public Result<BindBankCardResult> bindNewBankCard(BindBankCardParams bindBankCardParams){
        // 结果集
        Result<BindBankCardResult> result = new Result<>();

        try {
            // 银行卡列表
            return bankCardService.bindNewBankCard(bindBankCardParams);
        }catch (Exception e){
            LogUtil.sysError("[BankCardController bindNewBankCard]", e);

            // 异常返回结果
            result.setEx(ThrowableUtils.throwableToString(e));
            result.setReturnCode(ResultEnum.SYSTEM_ERROR.code());
            result.setMessage(ResultEnum.SYSTEM_ERROR.message());
            return result;
        }
    }

    /**
     * 确认旧卡
     *
     * @param bindBankCardParams 入参
     * @return 是否绑卡成功
     */
    @PostMapping(URL.CONFIRM_OLD_BANK_CARD)
    public Result<BindBankCardResult> confirmOldBankCard(BindBankCardParams bindBankCardParams){
        // 结果集
        Result<BindBankCardResult> result = new Result<>();

        try {
            // 银行卡列表
            return bankCardService.confirmOldBankCard(bindBankCardParams);
        }catch (Exception e){
            LogUtil.sysError("[BankCardController confirmOldBankCard]", e);

            // 异常返回结果
            result.setEx(ThrowableUtils.throwableToString(e));
            result.setReturnCode(ResultEnum.SYSTEM_ERROR.code());
            result.setMessage(ResultEnum.SYSTEM_ERROR.message());
            return result;
        }
    }

    /**
     * 确认旧卡并领款
     *
     * @param params 绑卡参数
     * @return 是否绑定成功
     */
    @PostMapping(URL.CONFIRM_OLD_BANK_CARD_LOAN)
    public Result<BindBankCardResult> confirmOldBankCardAndLoanAmount(BindBankCardParams params){
        // 结果集
        Result<BindBankCardResult> result = new Result<>();

        try {
            // 银行卡列表
            return bankCardService.confirmOldBankCardAndLoanAmount(params);
        }catch (Exception e){
            LogUtil.sysError("[UserController confirmOldBankCardAndLoanAmount]", e);

            // 异常返回结果
            result.setEx(ThrowableUtils.throwableToString(e));
            result.setReturnCode(ResultEnum.SYSTEM_ERROR.code());
            result.setMessage(ResultEnum.SYSTEM_ERROR.message());
            return result;
        }
    }

}
