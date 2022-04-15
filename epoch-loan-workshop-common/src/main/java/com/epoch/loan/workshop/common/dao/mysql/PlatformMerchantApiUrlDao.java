package com.epoch.loan.workshop.common.dao.mysql;

import com.epoch.loan.workshop.common.entity.mysql.PlatformMerchantApiUrlEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.dao.mysql
 * @className : PlatformMerchantApiUrlDao
 * @createTime : 2021/11/24 14:37
 * @description : 机构api地址
 */
@Mapper
public interface PlatformMerchantApiUrlDao {

    /**
     * 查询机构api地址
     *
     * @param merchantId
     * @return
     */
    PlatformMerchantApiUrlEntity findMerchantApiUrl(String merchantId);
}
