package com.epoch.loan.workshop.common.dao.mysql;

import com.epoch.loan.workshop.common.entity.mysql.LoanOrderEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.dao.mysql
 * @className : LoanOrderDao
 * @createTime : 2021/11/19 16:25
 * @description : 订单
 */
@Mapper
public interface LoanOrderDao {


    /**
     * 查询用户最后一条放款成功的订单
     *
     * @param userId
     * @return
     */
    LoanOrderEntity findLastUserRemittanceSuccessOrder(String userId);


    /**
     * 根据状态查询账单
     *
     * @param status
     * @return
     */
    List<LoanOrderEntity> findOrderByStatus(Integer status);

    /**
     * 根据订单ID查询订单
     *
     * @param orderId
     * @return
     */
    LoanOrderEntity findOrder(String orderId);

    /**
     * 修改订单状态
     *
     * @param orderId
     * @param status
     * @param updateTime
     * @return
     */
    int updateOrderStatus(String orderId, int status, Date updateTime);

    /**
     * 更新订单产品Id
     *
     * @param orderId
     * @param productId
     * @param updateTime
     * @return
     */
    int updateOrderProductId(String orderId, String productId, Date updateTime);

    /**
     * 更新批准金额
     *
     * @param orderId
     * @param approvalAmount
     * @param updateTime
     * @return
     */
    int updateOrderApprovalAmount(String orderId, double approvalAmount, Date updateTime);

    /**
     * 更新附带扣除费用
     *
     * @param orderId
     * @param incidentalAmount
     * @param updateTime
     * @return
     */
    int updateOrderIncidentalAmount(String orderId, double incidentalAmount, Date updateTime);

    /**
     * 更新用户客群
     *
     * @param orderId
     * @param userType
     * @param updateTime
     * @return
     */
    int updateOrderUserType(String orderId, Integer userType, Date updateTime);

    /**
     * 更新订单是否复贷
     *
     * @param orderId
     * @param reloan
     * @param updateTime
     * @return
     */
    int updateOrderReloan(String orderId, Integer reloan, Date updateTime);

    /**
     * 更新审核通过时间
     *
     * @param orderId
     * @param examinePassTime
     * @param updateTime
     * @return
     */
    int updateOrderExaminePassTime(String orderId, Date examinePassTime, Date updateTime);

    /**
     * 更新申请时间
     *
     * @param orderId
     * @param applyTime
     * @param updateTime
     * @return
     */
    int updateOrderApplyTime(String orderId, Date applyTime, Date updateTime);

    /**
     * 更改实际放款金额
     *
     * @param orderId
     * @param actualAmount
     * @param updateTime
     * @return
     */
    int updateOrderActualAmount(String orderId, double actualAmount, Date updateTime);

    /**
     * 更新放款时间
     *
     * @param orderId
     * @param loanTime
     * @param updateTime
     * @return
     */
    int updateOrderLoanTime(String orderId, Date loanTime, Date updateTime);

    /**
     * 更新时间还款金额
     *
     * @param orderId
     * @param actualRepaymentAmount
     * @param updateTime
     * @return
     */
    int updateOrderActualRepaymentAmount(String orderId, double actualRepaymentAmount, Date updateTime);

    /**
     * 更新到账时间
     *
     * @param orderId
     * @param arrivalTime
     * @param updateTime
     * @return
     */
    int updateOrderArrivalTime(String orderId, Date arrivalTime, Date updateTime);

    /**
     * 更新还款金额
     *
     * @param orderId
     * @param estimatedRepaymentAmount
     * @param updateTime
     * @return
     */
    int updateOrderEstimatedRepaymentAmount(String orderId, double estimatedRepaymentAmount, Date updateTime);

    /**
     * 查询用户订单数量
     *
     * @param userId
     * @param status
     * @return
     */
    int findOrderCountByUserIdAndStatus(String userId, int status);

    /**
     * 查询订单使用的支付策略组
     *
     * @param orderId 订单号
     * @return 支付策略组名称
     */
    String findRemittanceDistributionGroupById(String orderId);

    /**
     * 更新放款时间
     *
     * @param orderId
     * @param loanTime
     * @param updateTime
     */
    void updateLoanTime(String orderId, Date loanTime, Date updateTime);

    /**
     * 更新还款策略组
     *
     * @param orderId
     * @param repaymentDistributionGroup
     * @param updateTime
     */
    void updateOrderRepaymentDistributionGroup(String orderId, String repaymentDistributionGroup, Date updateTime);

    /**
     * 更新放款账户id
     *
     * @param orderId
     * @param remittanceAccountId
     * @param updateTime
     */
    void updateBankCardId(String orderId, String remittanceAccountId, Date updateTime);

    /**
     * 根据用户id查询指定状态的订单
     *
     * @param userId
     * @param status
     * @return
     */
    List<LoanOrderEntity> findOrderByUserIdAndStatus(String userId, @Param("array") Integer[] status);

    /**
     * 根据用户id查询指定状态的订单
     *
     * @param userId
     * @param productId
     * @param status
     * @return
     */
    List<LoanOrderEntity> findOrderByUserAndProductIdAndStatus(String userId, String productId, @Param("array") Integer[] status);

    /**
     * @param userId
     * @param status
     * @return
     */
    Integer countUserOrderByStatusIn(String userId, @Param("array") int[] status);

    /**
     * 查询用户在指定包|指定状态|指定产品的订单数量
     *
     * @param userId
     * @param productId
     * @param status
     * @return
     */
    Integer countUserOrderByProductAndStatusIn(String userId, String productId, @Param("array") int[] status);

    /**
     * 新增订单
     *
     * @param loanOrderEntity
     * @return
     */
    Integer insertOrder(@Param("loanOrderEntity") LoanOrderEntity loanOrderEntity);

    /**
     * 查询用户进行中订单数量
     *
     * @param userIdList  用户Id
     * @return
     */
    int countProcessOrderNo(@Param("userIdList") List<String> userIdList);

    /**
     * 小于指定状态的订单数量
     *
     * @param userId
     * @param start
     * @param end
     * @return
     */
    Integer findUserBetweenSpecificStatusOrderNum(String userId, Integer start, Integer end);

    /**
     * 根据用户id和订单类型查询订单数量
     *
     * @param userId
     * @param type
     * @return
     */
    Integer findOrderCountByUserIdAndType(String userId, int type);

    /**
     * 根据用户id查询指定状态的订单计划，并按照指定字段 指定顺序进行排列
     *
     * @param userId 用户id
     * @param status 订单状态集合
     * @param field 排序字段
     * @param sequence 正序-ASC 还是 倒序-DESC
     * @return
     */
    List<LoanOrderEntity> findOrderListByUserIdAndStatusAndOrderByField(String userId, @Param("array") Integer[] status, String field, String sequence);

    /**
     * 查询用户在该产品最后一笔订单
     *
     * @param userId
     * @param productId
     * @return
     */
    LoanOrderEntity findUserLastOrderWithProduct(String userId, String productId);

    /**
     * 根据用户Id产品id订单状态，查询距离当前时间最近的一笔订单
     *
     * @param userId
     * @param productId
     * @param status
     * @return
     */
    LoanOrderEntity findLatelyOrderByUserIdAndProductIdAndStatus(String userId, String productId, @Param("array") Integer[] status);

    /**
     * 根据用户Id产品id订单状态，查询距离当前时间最近的一笔订单
     *
     * @param userId
     * @param status
     * @return
     */
    LoanOrderEntity findLatelyOrderByUserIdAndStatus(String userId, @Param("array") Integer[] status);
}
