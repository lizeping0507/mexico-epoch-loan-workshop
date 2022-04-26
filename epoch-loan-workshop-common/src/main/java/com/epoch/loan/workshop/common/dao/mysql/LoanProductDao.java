package com.epoch.loan.workshop.common.dao.mysql;

import com.epoch.loan.workshop.common.entity.mysql.LoanProductEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.dao.mysql
 * @className : LoanProductDao
 * @createTime : 2022/2/11 11:17
 * @description : 产品
 */
@Mapper
public interface LoanProductDao {

    /**
     * 查询产品
     *
     * @param id
     * @return
     */
    LoanProductEntity findProduct(String id);

    /**
     * 查询所有产品
     *
     * @return
     */
    List<LoanProductEntity> findAll();

    /**
     * 查询用户无订单的产品
     *
     * @param userId
     * @return
     */
    List<LoanProductEntity> findProductWithoutUserOrder(String userId);
}
