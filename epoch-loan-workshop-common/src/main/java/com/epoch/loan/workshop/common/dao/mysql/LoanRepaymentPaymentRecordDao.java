package com.epoch.loan.workshop.common.dao.mysql;

import com.epoch.loan.workshop.common.entity.mysql.LoanRepaymentPaymentRecordEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.dao;
 * @className : LoanRepaymentPaymentRecordDao
 * @createTime : 2022/3/7 16:22
 * @description : 还款支付记录
 */
@Mapper
public interface LoanRepaymentPaymentRecordDao {
    /**
     * 保存
     *
     * @param paymentRecord 记录
     */
    void insert(@Param("paymentRecord") LoanRepaymentPaymentRecordEntity paymentRecord);

    /**
     * 更新代收记录详情请求参数和响应参数
     *
     * @param id         记录id
     * @param request    请求参数
     * @param response   响应参数
     * @param updateTime 更新时间
     */
    void updateRequestAndResponse(String id, String request, String response, Date updateTime);

    /**
     * 更新代收记录详情状态
     *
     * @param id         记录id
     * @param status     状态
     * @param updateTime 更新时间
     */
    void updateStatus(String id, int status, Date updateTime);

    /**
     * 根据订单账单id查询支付记录
     *
     * @param id
     * @return
     */
    LoanRepaymentPaymentRecordEntity findRepaymentPaymentRecordById(String id);

    /**
     * 修改查询请求响应参数
     *
     * @param id         id
     * @param request    请求
     * @param response   响应
     * @param updateTime 更新时间
     */
    void updateSearchRequestAndResponse(@Param("id") String id, @Param("request") String request, @Param("response") String response, @Param("updateTime") Date updateTime);

    /**
     * 存储业务所需id
     *
     * @param id         id
     * @param businessId 业务Id
     * @param updateTime 更新时间
     */
    void updateBussinesId(String id, String businessId, Date updateTime);

    /**
     * 根据订单账单ID查询已经实际支付金额
     *
     * @param orderBillId
     * @return
     */
    Double sumRepaymentRecordActualAmount(String orderBillId);

    /**
     * 更新订单还款记录里的 实际还款金额
     *
     * @param id           还款记录id
     * @param actualAmount 时间还款金额
     * @param updateTime   更新时间
     */
    void updateRepaymentPaymentRecordActualAmount(String id, double actualAmount, Date updateTime);

    /**
     * 根据 订单id 查询所有还款成功记录
     *
     * @param orderId 订单id
     * @param status 支付状态
     * @return 还款成功记录
     */
    List<LoanRepaymentPaymentRecordEntity> findListRecordDTOByOrderIdAndStatus(String orderId, Integer status);

    /**
     * 存储clabe和条形码
     *
     * @param id
     * @param clabe
     * @param barCode
     * @param updateTime
     */
    void updatePaymentRecordClabeAndBarCode(String id, String clabe, String barCode, Date updateTime);
}
