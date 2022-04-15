package com.epoch.loan.workshop.common.dao.mysql;

import com.epoch.loan.workshop.common.entity.mysql.LoanRepaymentDistributionEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.dao;
 * @className : LoanRepaymentDistributionDao
 * @createTime : 2022/3/7 16:21
 * @description : TODO
 */
@Mapper
public interface LoanRepaymentDistributionDao {

    /**
     * 查询 放款渠道配置
     *
     * @param groupName groupName
     * @return 放款渠道配置列表
     */
    List<LoanRepaymentDistributionEntity> findRepaymentDistribution(String groupName);
}
