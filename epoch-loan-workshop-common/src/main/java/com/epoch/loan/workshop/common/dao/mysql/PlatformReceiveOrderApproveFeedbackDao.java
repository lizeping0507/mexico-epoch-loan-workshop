package com.epoch.loan.workshop.common.dao.mysql;

import com.epoch.loan.workshop.common.entity.mysql.PlatformReceiveOrderApproveFeedbackEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.dao.mysql
 * @className : PlatformReceiveOrderApproveFeedbackDao
 * @createTime : 2021/11/19 17:43
 * @description : TODO 老表
 */
@Mapper
public interface PlatformReceiveOrderApproveFeedbackDao {
    /**
     * 保存
     *
     * @param approveFeedbackEntity 审批结果反馈接口信息
     */
    void save(@Param("approveFeedbackEntity") PlatformReceiveOrderApproveFeedbackEntity approveFeedbackEntity);
}
