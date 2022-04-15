package com.epoch.loan.workshop.common.dao.mysql;

import com.epoch.loan.workshop.common.entity.mysql.LoanProductEntity;
import org.apache.ibatis.annotations.Mapper;

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
}
