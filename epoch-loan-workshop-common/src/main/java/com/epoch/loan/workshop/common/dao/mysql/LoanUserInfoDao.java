package com.epoch.loan.workshop.common.dao.mysql;

import com.epoch.loan.workshop.common.entity.mysql.LoanUserInfoEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.dao.mysql
 * @className : LoanUserInfoDao
 * @createTime : 2022/4/18 14:56
 * @description : 用户详情
 */
@Mapper
public interface LoanUserInfoDao {
    /**
     * 插入新记录
     *
     * @param loanUserInfoEntity
     */
    void insert(LoanUserInfoEntity loanUserInfoEntity);
}
