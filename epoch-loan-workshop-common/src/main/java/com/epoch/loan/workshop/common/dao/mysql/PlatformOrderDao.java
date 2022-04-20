package com.epoch.loan.workshop.common.dao.mysql;

import com.epoch.loan.workshop.common.entity.mysql.PlatformOrderEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.dao.mysql
 * @className : PlatformOrderDao
 * @createTime : 2021/12/3 11:01
 * @description : 订单
 */
@Mapper
public interface PlatformOrderDao {

    /**
     * 更新订单状态
     *
     * @param orderNo
     * @param status
     * @param updateTime
     * @return
     */
    int updateOrderStatus(String orderNo, Integer status, Date updateTime);

    /**
     * 更新订单产品机构ID
     *
     * @param orderNo
     * @param merchantId
     * @param updateTime
     * @return
     */
    int updateOrderMerchantId(String orderNo, String merchantId, Date updateTime);

    /**
     * 更新订单产品ID
     *
     * @param orderNo
     * @param productId
     * @param updateTime
     * @return
     */
    int updateOrderProductId(String orderNo, String productId, Date updateTime);

    /**
     * 更新用户客群
     *
     * @param orderNo
     * @param userType
     * @param updateTime
     * @return
     */
    int updateOrderUserType(String orderNo, Integer userType, Date updateTime);

    /**
     * 更改订单审批金额
     *
     * @param orderNo
     * @param approvalAmount
     * @param updateTime
     * @return
     */
    int updateOrderApprovalAmount(String orderNo, double approvalAmount, Date updateTime);

    /**
     * 更新订单客群
     *
     * @param orderNo
     * @param reloan
     * @param updateTime
     * @return
     */
    int updateOrderReloan(String orderNo, Integer reloan, Date updateTime);

    /**
     * 更新订单审批时间
     *
     * @param orderNo
     * @param time
     * @param updateTime
     * @return
     */
    int updateOrderApprovalTime(String orderNo, Date time, Date updateTime);

    /**
     * 更新实际放款金额
     *
     * @param orderNo
     * @param receiveAmount
     * @param updateTime
     * @return
     */
    int updateOrderReceiveAmount(String orderNo, double receiveAmount, Date updateTime);

    /**
     * 更新放款时间
     *
     * @param orderNo
     * @param loanTime
     * @param updateTime
     * @return
     */
    int updateOrderLoanTime(String orderNo, Date loanTime, Date updateTime);

    /**
     * 查询用户在途订单数量
     *
     * @param userId
     * @return
     */
    int findWayOrderCountByUserId(Long userId);

    /**
     * 查询用户指定产品第一笔订单还款时间
     *
     * @param userId
     * @param productId
     * @return
     */
    Date findUserFirstOrderDateByUserIdAndProductId(String userId, String productId);

    /**
     * 查询用户第一笔订单还款时间
     *
     * @param userId
     * @return
     */
    Date findUserFirstOrderDateByUserId(String userId);

    /**
     * 查询订单所属App id
     *
     * @param orderNo 订单号
     * @return AppId
     */
    Long findAppIdByOrderNo(String orderNo);

    /**
     * 查询订单
     *
     * @param orderNo 订单号
     * @return PlatformOrderEntity
     */
    PlatformOrderEntity findByOrderNo(String orderNo);

    /**
     * 查询用户小于指定状态的订单
     * @param userId
     * @param status
     * @return
     */
    Integer findUserLessThanSpecificStatusOrderNum(String userId, int status);

    /**
     * 查询代还款状态订单
     * @param userId
     * @return
     */
    Integer findUserWaitRepaymentOrderNum(String userId);

    /**
     * 查询用户所有订单数量
     *
     * @param userId
     * @return
     */
    Integer findUserAllOrderNum(String userId);
}
