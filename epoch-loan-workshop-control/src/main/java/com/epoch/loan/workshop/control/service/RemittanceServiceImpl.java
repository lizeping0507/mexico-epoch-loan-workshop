package com.epoch.loan.workshop.control.service;

import com.epoch.loan.workshop.common.constant.ResultEnum;
import com.epoch.loan.workshop.common.entity.mysql.LoanOrderEntity;
import com.epoch.loan.workshop.common.entity.mysql.LoanRemittanceAccountEntity;
import com.epoch.loan.workshop.common.params.params.BaseParams;
import com.epoch.loan.workshop.common.params.params.result.RemittanceAccountListResult;
import com.epoch.loan.workshop.common.params.params.result.Result;
import com.epoch.loan.workshop.common.params.params.result.model.RemittanceAccountList;
import com.epoch.loan.workshop.common.service.RemittanceService;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.control.service
 * @className : RemittanceServiceImpl
 * @createTime : 2022/4/21 14:55
 * @description : 放款
 */
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
}
