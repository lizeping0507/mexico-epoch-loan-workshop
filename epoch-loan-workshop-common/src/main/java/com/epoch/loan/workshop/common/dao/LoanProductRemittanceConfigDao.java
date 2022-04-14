package com.epoch.loan.workshop.common.dao;

import com.epoch.loan.workshop.common.entity.LoanProductRemittanceConfigEntity;
import com.epoch.loan.workshop.common.entity.LoanRemittanceDistributionEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.dao
 * @className : LoanRemittanceDistributionDao
 * @createTime : 2021/12/18 17:46
 * @description : 放款分配配置
 */
@Mapper
public interface LoanProductRemittanceConfigDao {
    /**
     * 查询放款分配
     *
     * @param groupName
     * @return
     */
    LoanProductRemittanceConfigEntity findByGroupName(String groupName);
}
