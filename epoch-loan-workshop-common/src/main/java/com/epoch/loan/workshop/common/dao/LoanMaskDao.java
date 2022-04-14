package com.epoch.loan.workshop.common.dao;

import com.epoch.loan.workshop.common.entity.LoanMaskEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.dao
 * @className : LoanMaskDao
 * @createTime : 2021/11/22 15:42
 * @description : 变身包产品配置
 */
@Mapper
public interface LoanMaskDao {
    /**
     * 根据app名称和风控阈值查询产品id
     *
     * @param appName
     * @param level
     * @return
     */
    LoanMaskEntity findLoanMaskByAppNameAndLevel(String appName, String level);
}
