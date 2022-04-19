package com.epoch.loan.workshop.common.dao.mysql;

import com.epoch.loan.workshop.common.entity.mysql.LoanRemittancePaymentRecordEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.dao.mysql
 * @className : LoanPaymentLogDao
 * @createTime : 2021/12/18 17:46
 * @description : 订单支付记录详情表
 */
@Mapper
public interface LoanRemittancePaymentRecordDao {

    /**
     * 查询
     *
     * @param id id
     * @return LoanRemittancePaymentRecordEntity
     */
    LoanRemittancePaymentRecordEntity getById(String id);

    /**
     * 根据订单号查询
     *
     * @param orderId 订单号
     * @return LoanRemittancePaymentRecordEntity
     */
    LoanRemittancePaymentRecordEntity getByOrderId(String orderId);

    /**
     * 插入
     *
     * @param loanPaymentLogEntity 订单支付记录详情
     * @return 行数
     */
    int insert(@Param("loanPaymentLogEntity") LoanRemittancePaymentRecordEntity loanPaymentLogEntity);

    /**
     * 更新队列参数
     *
     * @param id         id
     * @param queueParam 队列参数json
     */
    void updateQueueParam(String id, String queueParam, Date updateTime);

    /**
     * 更新请求和响应信息
     *
     * @param id       id
     * @param request  请求信息
     * @param response 响应信息
     */
    void updateRequestAndResponse(String id, String request, String response, Date updateTime);

    /**
     * 更新状态
     *
     * @param id         id
     * @param status     状态
     * @param updateTime 时间
     */
    void updateStatus(String id, Integer status, Date updateTime);

    /**
     * 更新查询请求和响应信息
     *
     * @param id       id
     * @param request  请求信息
     * @param response 响应信息
     */
    void updateSearchRequestAndSearchResponse(String id, String request, String response, Date updateTime);

    /**
     * 更新所用配置标记
     *
     * @param id         id
     * @param configTag  配置标记
     * @param updateTime 更新时间
     */
    void updateRemittancePaymentRecordConfigTag(String id, String configTag, Date updateTime);

    /**
     * 根据支付记录Id 和 支付渠道Id 查询支付详情记录账号渠道标记
     *
     * @param remittanceOrderRecordId 支付记录Id
     * @param paymentId               支付渠道Id
     * @return List<String>
     */
    List<String> findByRemittanceOrderRecordIdAndPaymentId(String remittanceOrderRecordId, String paymentId);

    /**
     * 根据时间段查询 聚道发起支付数
     *
     * @param paymentId 聚道id
     * @param status    支付状态
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 发起支付数
     */
    int countByTime(@Param("paymentId") String paymentId, @Param("status") Integer status, @Param("startTime") Date startTime, @Param("endTime") Date endTime);

    /**
     * 通过Id查询
     *
     * @param id
     * @return
     */
    LoanRemittancePaymentRecordEntity findById(@Param("id") String id);

    /**
     * 查询小于指定状态 且放款记录id为指定Id的 放款详情条数
     *
     * @param maxStatus
     * @param recordId
     * @return
     */
    int countByRecordIdAndLatterThanStatus(int maxStatus, String recordId);
}
