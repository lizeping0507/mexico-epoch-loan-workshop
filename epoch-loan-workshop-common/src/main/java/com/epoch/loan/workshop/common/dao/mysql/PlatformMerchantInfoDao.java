package com.epoch.loan.workshop.common.dao.mysql;

import com.epoch.loan.workshop.common.entity.mysql.PlatformMerchantInfoEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.dao.mysql
 * @className : PlatformMerchantInfoDao
 * @createTime : 2021/11/24 14:17
 * @description : 机构详情
 */
@Mapper
public interface PlatformMerchantInfoDao {

    /**
     * 查询机构详情
     *
     * @param merchantId
     * @return
     */
    PlatformMerchantInfoEntity findMerchantInfo(String merchantId);
}
