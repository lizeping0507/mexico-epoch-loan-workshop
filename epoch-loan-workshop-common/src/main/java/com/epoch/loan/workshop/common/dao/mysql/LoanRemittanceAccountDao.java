package com.epoch.loan.workshop.common.dao.mysql;

import com.epoch.loan.workshop.common.entity.mysql.LoanRemittanceAccountEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.dao.mysql
 * @className : LoanRemittanceAccountDao
 * @createTime : 2022/4/21 15:08
 * @description : 放款账户
 */
@Mapper
public interface LoanRemittanceAccountDao {
    /**
     * 查询用户放款账户列表
     *
     * @param userId
     * @return
     */
    List<LoanRemittanceAccountEntity> findUserRemittanceAccountList(String userId);

    /**
     * 查询用户放款账户数量
     *
     * @param userId
     * @return
     */
    Integer findUserRemittanceAccountCount(String userId);

    /**
     * 查询放款账户
     *
     * @param id
     * @return
     */
    LoanRemittanceAccountEntity findRemittanceAccount(String id);

    /**
     * 查询指定放款卡账户
     * @param userId
     * @return
     */
    LoanRemittanceAccountEntity findLoanCard(String userId);

    /**
     * 新增放款账户
     *
     * @param loanRemittanceAccountEntity
     * @return
     */
    int addRemittanceAccount(@Param("loanRemittanceAccountEntity") LoanRemittanceAccountEntity loanRemittanceAccountEntity);

    /**
     * 通过卡号查询放款账户
     * @param accountNumber
     * @return
     */
    LoanRemittanceAccountEntity findByAccountNumber(String accountNumber);

    /**
     * 更新放款卡标识，仅最新增加的银行卡为放款卡
     * @param userId 入参
     * @return int
     */
    int updateMarkLoanCardStatus(String userId, Date updateTime);
}
