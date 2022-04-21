package com.epoch.loan.workshop.common.dao.mysql;

import com.epoch.loan.workshop.common.entity.mysql.LoanUserInfoEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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
    void insert(@Param("loanUserInfoEntity")LoanUserInfoEntity loanUserInfoEntity);

    /**
     * 查询用户详细信息
     *
     * @param userId 用户id
     * @return
     */
    LoanUserInfoEntity getByUserId(String userId);

    /**
     * 更新
     * @param loanUserInfoEntity
     */
    void update(@Param("loanUserInfoEntity") LoanUserInfoEntity loanUserInfoEntity);
}
