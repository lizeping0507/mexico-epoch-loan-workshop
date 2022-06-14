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
     * 根据订单id删除账单
     *
     * @param orderId
     * @return
     */
    int removeOrderBillByOrderId(String orderId);

    /**
     * 查询订单账单数量
     *
     * @param orderId
     * @return
     */
    Integer findOrderBillCountByOrderId(String orderId);

    /**
     * 查询结清-有逾期的订单账单数量
     *
     * @param orderId 订单id
     * @return 结清-有逾期的订单账单数量
     */
    int findOrderDueCompleteCount(String orderId);

    /**
     * 查询未完成的订单账单数量
     *
     * @param orderId 订单id
     * @return 订单账单数量
     */
    int findOrderNotCompleteCount(String orderId);

    /**
     * 查询已经完成订单还款总额
     *
     * @param orderId 订单id
     * @return 订单已还款总额
     */
    double sumOrderCompleteRepaymentAmount(String orderId);

    /**
     * 计算总还款金额
     *
     * @param orderId 订单id
     * @return 总还款金额
     */
    double sumOrderRepaymentAmount(String orderId);

    /**
     * 根据订单ID修改订单账单状态
     *
     * @param orderId 订单id
     * @param status 状态
     * @param updateTime 修改时间
     * @return 修改记录数
     */
    int updateOrderBillStatusByOrderId(String orderId, int status, Date updateTime);

    /**
     * 根据订单账单ID修改订单账单状态
     *
     * @param id 账单id
     * @param status 状态
     * @param updateTime 修改时间
     * @return 修改记录数
     */
    int updateOrderBillStatus(String id, int status, Date updateTime);

    /**
     * 更新还款金额
     *
     * @param id 账单id
     * @param repaymentAmount 还款金额
     * @param updateTime 修改时间
     * @return 修改记录数
     */
    int updateOrderBillRepaymentAmount(String id, double repaymentAmount, Date updateTime);

    /**
     * 更新还款时间
     *
     * @param id 账单id
     * @param repaymentTime 还款时间
     * @param updateTime 修改时间
     * @return 修改记录数
     */
    int updateOrderBillRepaymentTime(String id, Date repaymentTime, Date updateTime);

    /**
     * 更新实际还款时间
     *
     * @param id 账单id
     * @param actualRepaymentTime 实际还款时间
     * @param updateTime 修改时间
     * @return 修改记录数
     */
    int updateOrderBillActualRepaymentTime(String id, Date actualRepaymentTime, Date updateTime);

    /**
     * 查询订单账单列表
     *
     * @param orderId 订单id
     * @return 账单信息集合
     */
    List<LoanOrderBillEntity> findOrderBillByOrderId(String orderId);

    /**
     * 查询订单账单
     *
     * @param id 账单id
     * @return 账单信息
     */
    LoanOrderBillEntity findOrderBill(String id);

    /**
     * 更新罚息金额
     *
     * @param id 账单id
     * @param punishmentAmount 罚息金额
     * @param updateTime 修改时间
     * @return 修改记录数
     */
    int updateOrderBillPunishmentAmount(String id, double punishmentAmount, Date updateTime);

    /**
     * 更新减免金额
     *
     * @param id 账单id
     * @param reductionAmount 减免金额
     * @param updateTime 修改时间
     * @return 修改记录数
     */
    int updateOrderBillReductionAmount(String id, double reductionAmount, Date updateTime);

    /**
     * 更新实际付款金额
     *
     * @param id 账单id
     * @param receivedAmount 实际付款金额
     * @param updateTime 修改时间
     */
    void updateOrderBillReceivedAmount(String id, double receivedAmount, Date updateTime);

    /**
     * 查询所有指定状态及还款实际小于指定时间的订单账单
     *
     * @param status 状态
     * @param repaymentTime 还款时间
     * @return 订单账单集合
     */
    List<LoanOrderBillEntity> findOrderBillByStatusAndRepaymentTime(int status, Date repaymentTime);

    /**
     * 查询所有指定状态的订单账单
     *
     * @param status 订单状态
     * @return 订单账单集合
     */
    List<LoanOrderBillEntity> findOrderBillByStatus(int status);

    /**
     * 薪资订单账单
     *
     * @param loanOrderBillEntity 订单账单
     * @return 保存记录数
     */
    int insertOrderBill(@Param("loanOrderBillEntity") LoanOrderBillEntity loanOrderBillEntity);

    /**
     * 更新类型
     *
     * @param id 账单id
     * @param type 订单类型
     * @param updateTime 修改时间
     */
    void updateType(String id, int type, Date updateTime);

    /**
     * 更新附加费用
     *
     * @param id 账单id
     * @param incidentalAmount 附加费用金额
     * @param updateTime 修改时间
     */
    void updateOrderBillIncidentalAmount(String id, double incidentalAmount, Date updateTime);

    /**
     * 更新本期应还利息
     *
     * @param id 账单id
     * @param interestAmount 应还利息金额
     * @param updateTime 修改时间
     */
    void updateInterestAmount(String id, double interestAmount, Date updateTime);

    /**
     * 按天查询
     *
     * @param createTime yyyy-MM-dd%
     * @return 账单集合
     */
    List<LoanOrderBillEntity> findByDay(String createTime);

    /**
     * 修改本金
     * @param id 账单id
     * @param principalAmount 修改的本金金额
     * @param updateTime 修改时间
     */
    void updateOrderBillPrincipalAmount(String id, Double principalAmount, Date updateTime);

    /**
     * 查询第一笔还款订单
     * @param userId 用户id
     * @param appName app标识
     * @return 订单账单信息
     */
    LoanOrderBillEntity findFistRepayOrder(String userId, String appName);

    /**
     * 计算总罚息
     *
     * @param orderId 订单号
     * @return 总罚息
     */
    Double sumOrderPunishmentAmount(String orderId);

    /**
     * 计算总利息
     * @param orderId 订单号
     * @return 总利息
     */
    Double sumOrderInterestAmount(String orderId);

    /**
     * 计算总服务费
     * @param orderId 订单号
     * @return 总服务费
     */
    Double sumIncidentalAmount(String orderId);

    /**
     * 计算总减免费用
     * @param orderId
     * @return 总减免费用
     */
    Double sumReductionAmount(String orderId);

    /**
     * 查询订单最后一期账单
     *
     * @param orderId 订单号
     * @return 最后一期账单信息
     */
    LoanOrderBillEntity findLastOrderBill(String orderId);

    /**
     * 查询指定状态订单并只返回最早的一期账单
     *
     * @param orderId 订单号
     * @param statusArray 查询指定状态订单并只返回最早的一期账单信息
     * @return 查询指定状态订单并只返回最早的一期账单
     */
    LoanOrderBillEntity findOrderBillFastStagesByStatusAndOrderId(String orderId, @Param("array") Integer[] statusArray);
}
