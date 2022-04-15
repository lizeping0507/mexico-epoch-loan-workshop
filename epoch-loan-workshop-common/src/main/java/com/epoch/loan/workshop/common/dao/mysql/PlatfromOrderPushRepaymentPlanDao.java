package com.epoch.loan.workshop.common.dao.mysql;

import com.epoch.loan.workshop.common.entity.mysql.PlatfromOrderPushRepaymentPlanEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.dao.mysql
 * @className : PlatfromOrderPushRepaymentPlanDao
 * @createTime : 2021/11/24 11:07
 * @description : 还款计划推送接口表-计划子表 ?
 */
@Mapper
public interface PlatfromOrderPushRepaymentPlanDao {
    /**
     * 新增
     *
     * @param orderPushRepaymentPlanEntity
     */
    public void insert(@Param("orderPushRepaymentPlanEntity") PlatfromOrderPushRepaymentPlanEntity orderPushRepaymentPlanEntity);

    /**
     * 获取最大Id
     *
     * @return
     */
    long getMaxId();
}
