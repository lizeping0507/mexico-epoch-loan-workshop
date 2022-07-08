package com.epoch.loan.workshop.common.dao.mysql;

import com.epoch.loan.workshop.common.entity.mysql.LoanUserBankCardEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author : ljy
 * @packageName : com.epoch.loan.workshop.common.dao;
 * @className : LoanUserBankCardDao
 * @createTime : 2022/3/7 16:22
 * @description : 用户放款卡
 */
@Mapper
public interface LoanUserBankCardDao {

    /**
     * 查询用户放款卡信息
     * @param userId 用户id
     * @return LoanUserBankCardEntity
     */
    LoanUserBankCardEntity findByUserId(String userId);
}
