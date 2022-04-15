package com.epoch.loan.workshop.common.dao.mysql;

import com.epoch.loan.workshop.common.entity.mysql.PlatformMerchantEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.dao.mysql
 * @className : PlatformMerchantDao
 * @createTime : 2021/11/24 14:17
 * @description : 机构
 */
@Mapper
public interface PlatformMerchantDao {

    /**
     * 查询机构
     *
     * @param merchantId
     * @return
     */
    PlatformMerchantEntity findMerchant(String merchantId);
}
