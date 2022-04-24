package com.epoch.loan.workshop.common.dao.mysql;

import com.epoch.loan.workshop.common.entity.mysql.LoanOrderBillEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.dao.mysql
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

    /**
     * 更新罚息金额
     *
     * @param id
     * @param punishmentAmount
     * @param updateTime
     * @return
     */
    int updateOrderBillPunishmentAmount(String id, double punishmentAmount, Date updateTime);

    /**
     * 更新减免金额
     *
     * @param id
     * @param reductionAmount
     * @param updateTime
     * @return
     */
    int updateOrderBillReductionAmount(String id, double reductionAmount, Date updateTime);

    /**
     * 更新实际付款金额
     *
     * @param id
     * @param receivedAmount
     * @param updateTime
     */
    void updateOrderBillReceivedAmount(String id, double receivedAmount, Date updateTime);

    /**
     * 查询所有指定状态及还款实际小于指定时间的订单账单
     *
     * @param status
     * @param repaymentTime
     * @return
     */
    List<LoanOrderBillEntity> findOrderBillByStatusAndRepaymentTime(int status, Date repaymentTime);

    /**
     * 查询所有指定状态的订单账单
     *
     * @param status
     * @return
     */
    List<LoanOrderBillEntity> findOrderBillByStatus(int status);

    /**
     * 薪资订单账单
     *
     * @param loanOrderBillEntity
     * @return
     */
    int insertOrderBill(@Param("loanOrderBillEntity") LoanOrderBillEntity loanOrderBillEntity);

    /**
     * 更新类型
     *
     * @param id
     * @param type
     * @param updateTime
     */
    void updateType(String id, int type, Date updateTime);

    /**
     * 更新附加费用
     *
     * @param id
     * @param incidentalAmount
     * @param updateTime
     */
    void updateOrderBillIncidentalAmount(String id, double incidentalAmount, Date updateTime);

    /**
     * 更新本期应还利息
     *
     * @param id
     * @param interestAmount
     * @param updateTime
     */
    void updateInterestAmount(String id, double interestAmount, Date updateTime);

    /**
     * 按天查询
     *
     * @param createTime yyyy-MM-dd%
     * @return
     */
    List<LoanOrderBillEntity> findByDay(String createTime);

    void updateOrderBillPrincipalAmount(String id, Double principalAmount, Date updateTime);

    /**
     * 查询第一笔还款订单
     * @param userId
     * @param appName
     * @return
     */
    LoanOrderBillEntity findFistRepayOrder(String userId, String appName);

    /**
     * 计算总罚息
     *
     * @param orderId
     * @return
     */
    Double sumOrderPunishmentAmount(String orderId);

    /**
     * 计算总利息
     * @param orderId
     * @return
     */
    Double sumOrderInterestAmount(String orderId);

    /**
     * 查询订单最后一期账单
     *
     * @param orderId
     * @return
     */
    LoanOrderBillEntity findLastOrderBill(String orderId);
}
