package com.epoch.loan.workshop.common.dao.mysql;

import com.epoch.loan.workshop.common.entity.mysql.LoanRemittanceBankEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.dao.mysql
 * @className : LoanRemittanceBankDao
 * @createTime : 2022/4/21 16:28
 * @description : 放款银行
 */
@Mapper
public interface LoanRemittanceBankDao {

    /**
     * 查询放款银行列表
     *
     * @return
     */
    List<LoanRemittanceBankEntity> findLoanRemittanceBankList();
}
