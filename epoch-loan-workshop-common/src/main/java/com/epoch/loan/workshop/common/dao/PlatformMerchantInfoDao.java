package com.epoch.loan.workshop.common.dao;

import com.epoch.loan.workshop.common.entity.PlatformMerchantEntity;
import com.epoch.loan.workshop.common.entity.PlatformMerchantInfoEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.dao
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
