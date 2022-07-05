package com.epoch.loan.workshop.common.dao.mysql;

import com.epoch.loan.workshop.common.entity.mysql.LoanUserModelEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author : ljy
 * @packageName : com.epoch.loan.workshop.common.dao.mysql
 * @className : LoanUserModelDao
 * @createTime : 2021/11/19 16:25
 * @description : 用户模式
 */
@Mapper
public interface LoanUserModelDao {

    Integer findByUserId(String userId);

    Integer addLoanUserModel(@Param("LoanUserModelEntity") LoanUserModelEntity loanUserModelEntity);

}
