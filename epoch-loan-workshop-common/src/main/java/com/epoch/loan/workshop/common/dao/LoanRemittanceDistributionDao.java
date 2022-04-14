package com.epoch.loan.workshop.common.dao;

import com.epoch.loan.workshop.common.entity.LoanRemittanceDistributionEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.dao
 * @className : LoanRemittanceDistributionDao
 * @createTime : 2021/12/18 17:46
 * @description : 放款分配
 */
@Mapper
public interface LoanRemittanceDistributionDao {
    /**
     * 查询放款分配
     *
     * @param groupName
     * @return
     */
    List<LoanRemittanceDistributionEntity> findRemittanceDistribution(String groupName);
}
