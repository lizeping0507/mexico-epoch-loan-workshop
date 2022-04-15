package com.epoch.loan.workshop.common.dao.mysql;

import com.epoch.loan.workshop.common.entity.mysql.PlatformRiskManagementRefuseReasonEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.dao.mysql
 * @className : PlatformRiskManagementRefuseReason
 * @createTime : 2022/1/26 10:38
 * @description : 风控监控
 */
@Mapper
public interface PlatformRiskManagementRefuseReasonDao {
    /**
     * 新增风控监控记录
     *
     * @param platformRiskManagementRefuseReasonEntity 风控规则监控
     * @return 行数
     */
    int insert(@Param("platformRiskManagementRefuseReasonEntity") PlatformRiskManagementRefuseReasonEntity platformRiskManagementRefuseReasonEntity);
}
