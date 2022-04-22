package com.epoch.loan.workshop.common.dao.mysql;

import com.epoch.loan.workshop.common.entity.mysql.PlatformOrderPushRepaymentEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.dao.mysql
 * @className : PlatformOrderPushRepaymentDao
 * @createTime : 2021/11/24 11:07
 * @description : 还款计划推送接口表 ?
 */
@Mapper
public interface PlatformOrderPushRepaymentDao {
    /**
     * 新增
     *
     * @param orderPushRepayment
     */
    void insert(@Param("orderPushRepayment") PlatformOrderPushRepaymentEntity orderPushRepayment);

    /**
     * 获取最大Id
     *
     * @return
     */
    long getMaxId();

}
