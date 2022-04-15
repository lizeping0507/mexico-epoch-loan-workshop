package com.epoch.loan.workshop.common.dao.mysql;

import com.epoch.loan.workshop.common.entity.mysql.LoanProductRepaymentConfigEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.dao;
 * @className : LoanProductRepaymentConfigDao
 * @createTime : 2022/3/7 16:21
 * @description : TODO
 */
@Mapper
public interface LoanProductRepaymentConfigDao {
    /**
     * 查询策略
     *
     * @param groupName groupName
     * @return 策略
     */
    LoanProductRepaymentConfigEntity findByGroupName(String groupName);
}
