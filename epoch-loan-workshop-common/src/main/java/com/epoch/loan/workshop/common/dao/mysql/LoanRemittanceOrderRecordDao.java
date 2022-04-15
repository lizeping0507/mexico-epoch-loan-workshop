package com.epoch.loan.workshop.common.dao.mysql;

import com.epoch.loan.workshop.common.entity.mysql.LoanRemittanceOrderRecordEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.dao.mysql
 * @className : LoanRemittanceOrderRecordDao
 * @createTime : 2021/12/18 17:46
 * @description : 订单支付记录
 */
@Mapper
public interface LoanRemittanceOrderRecordDao {

    /**
     * 根据Id查询放款记录
     *
     * @param id id
     * @return LoanRemittanceOrderRecordEntity
     */
    LoanRemittanceOrderRecordEntity getById(String id);

    /**
     * 新增订单支付记录
     *
     * @param loanRemittanceOrderRecordEntity 订单支付记录
     */
    void insert(@Param("loanRemittanceOrderRecordEntity") LoanRemittanceOrderRecordEntity loanRemittanceOrderRecordEntity);

    /**
     * 根据订单id查询订单支付记录状态
     *
     * @param orderId
     * @return
     */
    Integer findLoanRemittanceOrderRecordStatusByOrderId(String orderId);

    /**
     * 查询订单支付记录状态
     *
     * @param id id
     * @return
     */
    Integer findLoanRemittanceOrderRecordStatusById(String id);

    /**
     * 更新状态
     *
     * @param id         id
     * @param status     状态
     * @param updateTime 更新时间
     */
    void updateStatus(String id, int status, Date updateTime);

    /**
     * 更新支付渠道
     *
     * @param id         id
     * @param paymentId  渠道Id
     * @param updateTime 更新时间
     */
    void updatePaymentId(String id, String paymentId, Date updateTime);

    /**
     * 更新进行中订单详情Id
     *
     * @param id                               id
     * @param processRemittancePaymentRecordId 进行中订单详情Id
     * @param updateTime                       更新时间
     */
    void updateProcessRemittancePaymentRecordId(String id, String processRemittancePaymentRecordId, Date updateTime);

    /**
     * 更新成功订单详情Id
     *
     * @param id                               id
     * @param successRemittancePaymentRecordId 成功订单详情Id
     * @param updateTime                       更新时间
     */
    void updateSuccessRemittancePaymentRecordId(String id, String successRemittancePaymentRecordId, Date updateTime);

    /**
     * 查询指定状态记录
     *
     * @param status 状态
     * @return List<LoanRemittanceOrderRecordEntity>
     */
    List<LoanRemittanceOrderRecordEntity> findByStatus(int status);
}
