package com.epoch.loan.workshop.control.service;

import com.alibaba.fastjson.JSONObject;
import com.epoch.loan.workshop.common.constant.Field;
import com.epoch.loan.workshop.common.constant.ResultEnum;
import com.epoch.loan.workshop.common.entity.mysql.LoanOrderEntity;
import com.epoch.loan.workshop.common.entity.mysql.LoanRemittanceAccountEntity;
import com.epoch.loan.workshop.common.entity.mysql.LoanRemittanceBankEntity;
import com.epoch.loan.workshop.common.params.params.BaseParams;
import com.epoch.loan.workshop.common.params.params.request.AddRemittanceAccountParams;
import com.epoch.loan.workshop.common.params.params.result.AddRemittanceAccountResult;
import com.epoch.loan.workshop.common.params.params.result.RemittanceAccountListResult;
import com.epoch.loan.workshop.common.params.params.result.RemittanceBankListResult;
import com.epoch.loan.workshop.common.params.params.result.Result;
import com.epoch.loan.workshop.common.params.params.result.model.RemittanceAccountList;
import com.epoch.loan.workshop.common.service.RemittanceService;
import com.epoch.loan.workshop.common.util.HttpUtils;
import com.epoch.loan.workshop.common.util.ObjectIdUtil;
import com.epoch.loan.workshop.common.util.RSAUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;

import java.util.*;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.control.service
 * @className : RemittanceServiceImpl
 * @createTime : 2022/4/21 14:55
 * @description : 放款
 */
@DubboService(timeout = 5000)
public class RemittanceServiceImpl extends BaseService implements RemittanceService {
    /**
     * 放款账户列表
     *
     * @param baseParams
     * @return
     */
    @Override
    public Result<RemittanceAccountListResult> remittanceAccountList(BaseParams baseParams) {
        // 用户id
        String userId = baseParams.getUser().getId();

        // 查询最后一条放款成功订单
        LoanOrderEntity loanOrderEntity = loanOrderDao.findLastUserRemittanceSuccessOrder(userId);

        //
        if (ObjectUtils.isEmpty(loanOrderEntity)){
            // 封装结果集
            Result<RemittanceAccountListResult> result = new Result<>();
            RemittanceAccountListResult remittanceAccountListResult = new RemittanceAccountListResult();
            remittanceAccountListResult.setList(new ArrayList<>());
            result.setData(remittanceAccountListResult);
            result.setReturnCode(ResultEnum.SUCCESS.code());
            result.setMessage(ResultEnum.SUCCESS.message());
            return result;
        }

        // 查询最后一条放款成功的订单的放款账户
        LoanRemittanceAccountEntity lastUserLoanRemittanceAccountEntity = loanRemittanceAccountDao.findRemittanceAccount(loanOrderEntity.getBankCardId());

        // 查询用户放款账户列表
        List<LoanRemittanceAccountEntity> loanRemittanceAccountEntityList = loanRemittanceAccountDao.findUserRemittanceAccountList(userId);

        // 按照最后一条放款的账户在第一条进行排序
        List<RemittanceAccountList> remittanceAccountLists = new ArrayList<>();
        RemittanceAccountList remittanceAccountList = new RemittanceAccountList();
        BeanUtils.copyProperties(lastUserLoanRemittanceAccountEntity, remittanceAccountList);
        remittanceAccountLists.add(remittanceAccountList);
        for (LoanRemittanceAccountEntity loanRemittanceAccountEntity : loanRemittanceAccountEntityList) {
            // 判断是否是最后一次放款成功的订单放款账户
            if (loanRemittanceAccountEntity.getId().equals(lastUserLoanRemittanceAccountEntity.getId())) {
                continue;
            }

            remittanceAccountList = new RemittanceAccountList();
            BeanUtils.copyProperties(loanRemittanceAccountEntity, remittanceAccountList);
            remittanceAccountLists.add(remittanceAccountList);
        }

        // 封装结果集
        Result<RemittanceAccountListResult> result = new Result<>();
        RemittanceAccountListResult remittanceAccountListResult = new RemittanceAccountListResult();
        remittanceAccountListResult.setList(remittanceAccountLists);
        result.setData(remittanceAccountListResult);
        result.setReturnCode(ResultEnum.SUCCESS.code());
        result.setMessage(ResultEnum.SUCCESS.message());
        return result;
    }

    /**
     * 新增放款账户
     *
     * @param addRemittanceAccountParams
     * @return
     * @throws Exception
     */
    @Override
    public Result<AddRemittanceAccountResult> addRemittanceAccount(AddRemittanceAccountParams addRemittanceAccountParams) throws Exception {
        // 响应结果
        Result<AddRemittanceAccountResult> result = new Result();

        // 账户账号
        String accountNumber = addRemittanceAccountParams.getAccountNumber();

        // 银行
        String bank = addRemittanceAccountParams.getBank();

        // 姓名
        String name = addRemittanceAccountParams.getName();

        // 账户类型
        Integer type = addRemittanceAccountParams.getType();

        // 用户id
        String userId = addRemittanceAccountParams.getUser().getId();

        // 手机号
        String mobile = addRemittanceAccountParams.getUser().getMobile();

        // 请求风控验卡
        JSONObject verifyRemittanceAccountJson = verifyRemittanceAccount(userId, mobile, accountNumber);

        // 验证状态
        String status = verifyRemittanceAccountJson.getString("status");
        if (!status.equals("VERIFIED")) {
            result.setReturnCode(ResultEnum.REMITTANCE_ACCOUNT_ERROR.code());
            result.setMessage(ResultEnum.REMITTANCE_ACCOUNT_ERROR.message());
            return result;
        }

        // 新增银行卡
        String id = ObjectIdUtil.getObjectId();
        LoanRemittanceAccountEntity loanRemittanceAccountEntity = new LoanRemittanceAccountEntity();
        loanRemittanceAccountEntity.setId(id);
        loanRemittanceAccountEntity.setAccountNumber(accountNumber);
        loanRemittanceAccountEntity.setBank(bank);
        loanRemittanceAccountEntity.setUserId(userId);
        loanRemittanceAccountEntity.setName(name);
        loanRemittanceAccountEntity.setType(type);
        loanRemittanceAccountEntity.setUpdateTime(new Date());
        loanRemittanceAccountEntity.setCreateTime(new Date());
        loanRemittanceAccountDao.addRemittanceAccount(loanRemittanceAccountEntity);

        // 封装结果
        AddRemittanceAccountResult addRemittanceAccountResult = new AddRemittanceAccountResult();
        addRemittanceAccountResult.setId(id);
        result.setReturnCode(ResultEnum.SUCCESS.code());
        result.setMessage(ResultEnum.SUCCESS.message());
        result.setData(addRemittanceAccountResult);
        return result;
    }

    /**
     * 验证银行卡号
     *
     * @param userId
     * @param mobile
     * @param accountNumber
     * @return
     */
    public JSONObject verifyRemittanceAccount(String userId, String mobile, String accountNumber) throws Exception {
        // 封装请求参数
        Map<String, String> params = new HashMap<>();
        params.put(Field.METHOD, "riskmanagement.mexico.bank.account.auth");
        params.put(Field.APP_ID, riskConfig.getAppId());
        params.put(Field.VERSION, "1.0");
        params.put(Field.SIGN_TYPE, "RSA");
        params.put(Field.FORMAT, "json");
        params.put(Field.TIMESTAMP, String.valueOf(System.currentTimeMillis() / 1000));

        JSONObject bizData = new JSONObject();
        bizData.put("transactionId", userId);
        bizData.put("clabeNumber", accountNumber);
        bizData.put("mobile", mobile);
        params.put(Field.BIZ_DATA, bizData.toJSONString());

        // 生成签名
        String paramsStr = RSAUtils.getSortParams(params);
        String sign = RSAUtils.addSign(riskConfig.getPrivateKey(), paramsStr);
        params.put(Field.SIGN, sign);

        // 请求参数
        String requestParams = JSONObject.toJSONString(params);

        // 发送请求
        String result = HttpUtils.POST_FORM(riskConfig.getRiskUrl(), requestParams);
        if (StringUtils.isEmpty(result)) {
            return null;
        }

        return JSONObject.parseObject(result).getJSONObject("biz_data");
    }


    /**
     * 银行账户列表
     *
     * @param baseParams
     * @return
     */
    @Override
    public Result<RemittanceBankListResult> remittanceBankList(BaseParams baseParams) {
        // 查询放款银行列表
        List<LoanRemittanceBankEntity> loanRemittanceBankList = loanRemittanceBankDao.findLoanRemittanceBankList();

        // 封装参数
        List<String> bankList = new ArrayList<>();
        loanRemittanceBankList.parallelStream().forEach(loanRemittanceBankEntity -> {
            bankList.add(loanRemittanceBankEntity.getName());
        });

        // 封装结果集
        Result<RemittanceBankListResult> result = new Result<>();
        RemittanceBankListResult remittanceBankListResult = new RemittanceBankListResult();
        remittanceBankListResult.setList(bankList);
        result.setData(remittanceBankListResult);
        result.setReturnCode(ResultEnum.SUCCESS.code());
        result.setMessage(ResultEnum.SUCCESS.message());
        return result;
    }


}
