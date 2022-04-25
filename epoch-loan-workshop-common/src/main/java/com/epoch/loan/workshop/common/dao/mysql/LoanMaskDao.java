package com.epoch.loan.workshop.common.dao.mysql;

import com.epoch.loan.workshop.common.entity.mysql.LoanMaskEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.dao.mysql
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

    /**
     * 根据产品id和App名称查询变身包承接盘信息
     *
     * @param appName
     * @param productId
     * @return
     */
    LoanMaskEntity findLoanMaskByAppNameAndProductId(String appName, String productId);

    /**
     * 查询指定包之外的所有承接盘配置
     *
     * @param appName
     * @return
     */
    List<LoanMaskEntity> findLoanMaskByAppNameIsNot(String appName);
}
