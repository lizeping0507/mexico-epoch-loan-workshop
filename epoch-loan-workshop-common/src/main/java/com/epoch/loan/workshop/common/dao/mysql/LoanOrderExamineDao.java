package com.epoch.loan.workshop.common.dao.mysql;

import com.epoch.loan.workshop.common.entity.mysql.LoanOrderExamineEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;
import java.util.List;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.dao.mysql
 * @className : LoanOrderExamineDao
 * @createTime : 2021/11/22 14:44
 * @description : 订单模型审核记录
 */
@Mapper
public interface LoanOrderExamineDao {
    /**
     * 查询订单模型审核状态
     *
     * @param orderId
     * @param modelName
     * @return
     */
    int findOrderExamineStatus(String orderId, String modelName);

    /**
     * 更新模型审核请求参数
     *
     * @param orderId
     * @param modelName
     * @param request
     * @param updateTime
     * @return
     */
    int updateOrderExamineRequest(String orderId, String modelName, String request, Date updateTime);

    /**
     * 更新模型审核响应参数
     *
     * @param orderId
     * @param modelName
     * @param response
     * @param updateTime
     * @return
     */
    int updateOrderExamineResponse(String orderId, String modelName, String response, Date updateTime);

    /**
     * 更改模型审核状态
     *
     * @param orderId
     * @param modelName
     * @param status
     * @param updateTime
     * @return
     */
    int updateOrderExamineStatus(String orderId, String modelName, Integer status, Date updateTime);

    /**
     * 根据modelName & status & datePoint 查询列表
     *
     * @param modelName modelName
     * @param status    status
     * @param datePoint datePoint
     * @return List<LoanTimingEntity>
     */
    List<LoanOrderExamineEntity> findByModelNameAndStatusBeforTime(String modelName, int status, Date datePoint);


    /**
     * 根据orderId  & ModelName 查询
     *
     * @param orderId
     * @param modelName
     * @return
     */
    LoanOrderExamineEntity findByModelNameAndOrderId(String orderId, String modelName);
}
