package com.epoch.loan.workshop.common.dao.mysql;

import com.epoch.loan.workshop.common.entity.mysql.PlatformProductEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.dao.mysql
 * @className : PlatformProductDao
 * @createTime : 2021/11/24 10:59
 * @description : 产品
 */
@Mapper
public interface PlatformProductDao {

    /**
     * 查询产品信息
     *
     * @param productId
     * @return
     */
    PlatformProductEntity findProduct(String productId);
}
