package com.epoch.loan.workshop.common.dao;

import com.epoch.loan.workshop.common.entity.LoanOrderBillEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.dao
 * @className : LoanOrderBillDao
 * @createTime : 2021/11/25 17:28
 * @description : 订单账单
 */
@Mapper
public interface LoanOrderBillDao {

    /**
     * 查询结清-有逾期的订单账单数量
     *
     * @param orderId
     * @return
     */
    int findOrderDueCompleteCount(String orderId);

    /**
     * 查询未完成的订单账单数量
     *
     * @param orderId
     * @return
     */
    int findOrderNotCompleteCount(String orderId);

    /**
     * 查询已经完成订单还款总额
     *
     * @param orderId
     * @return
     */
    double sumOrderCompleteRepaymentAmount(String orderId);

    /**
     * 计算总还款金额
     *
     * @param orderId
     * @return
     */
    double sumOrderRepaymentAmount(String orderId);

    /**
     * 薪资订单账单
     *
     * @param loanOrderBillEntity
     * @return
     */
    int insertOrderBillByOrderId(@Param("loanOrderBillEntity") LoanOrderBillEntity loanOrderBillEntity);

    /**
     * 根据订单ID修改订单账单状态
     *
     * @param orderId
     * @param status
     * @param updateTime
     * @return
     */
    int updateOrderBillStatusByOrderId(String orderId, int status, Date updateTime);

    /**
     * 根据订单账单ID修改订单账单状态
     *
     * @param id
     * @param status
     * @param updateTime
     * @return
     */
    int updateOrderBillStatus(String id, int status, Date updateTime);

    /**
     * 更新还款金额
     *
     * @param id
     * @param repaymentAmount
     * @param updateTime
     * @return
     */
    int updateOrderBillRepaymentAmount(String id, double repaymentAmount, Date updateTime);

    /**
     * 更新还款时间
     *
     * @param id
     * @param repaymentTime
     * @param updateTime
     * @return
     */
    int updateOrderBillRepaymentTime(String id, Date repaymentTime, Date updateTime);

    /**
     * 更新实际还款时间
     *
     * @param id
     * @param actualRepaymentTime
     * @param updateTime
     * @return
     */
    int updateOrderBillActualRepaymentTime(String id, Date actualRepaymentTime, Date updateTime);

    /**
     * 查询订单账单列表
     *
     * @param orderId
     * @return
     */
    List<LoanOrderBillEntity> findOrderBillByOrderId(String orderId);

    /**
     * 查询订单账单
     *
     * @param id
     * @return
     */
    LoanOrderBillEntity findOrderBill(String id);
}
