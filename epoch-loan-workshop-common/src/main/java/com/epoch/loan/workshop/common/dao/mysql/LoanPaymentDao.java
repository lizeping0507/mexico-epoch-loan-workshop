package com.epoch.loan.workshop.common.dao.mysql;

import com.epoch.loan.workshop.common.entity.mysql.LoanPaymentEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.dao.mysql
 * @className : LoanPaymentDao
 * @createTime : 2021/12/18 17:46
 * @description : 支付渠道
 */
@Mapper
public interface LoanPaymentDao {

    /**
     * 根据Id查询放款渠道
     *
     * @param id 渠道Id
     * @return LoanPaymentEntity
     */
    LoanPaymentEntity getById(String id);

    /**
     * 查询所有在用聚道
     *
     * @return 聚道列表
     */
    List<LoanPaymentEntity> selectAll();
}
