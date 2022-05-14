package com.epoch.loan.workshop.common.dao.mysql;

import com.epoch.loan.workshop.common.entity.mysql.LoanProductExtEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.dao.mysql
 * @className : LoanProductDao
 * @createTime : 2022/2/11 11:17
 * @description : 产品扩展信息
 */
@Mapper
public interface LoanProductExtDao {

    /**
     * 查询产品扩展配置
     *
     * @param productId
     * @return 产品扩展信息
     */
    LoanProductExtEntity findByProductId(String productId);
}
