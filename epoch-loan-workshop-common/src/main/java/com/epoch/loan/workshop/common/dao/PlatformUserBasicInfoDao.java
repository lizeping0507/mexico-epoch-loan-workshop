package com.epoch.loan.workshop.common.dao;

import com.epoch.loan.workshop.common.entity.PlatformUserBasicInfoEntity;
import com.epoch.loan.workshop.common.entity.PlatformUserOcrBasicInfoEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.dao
 * @className : PlatformUserBasicInfoDao
 * @createTime : 2021/11/19 17:09
 * @description : 用户基本信息 TODO 老表
 */
@Mapper
public interface PlatformUserBasicInfoDao {

    /**
     * 查询用户基本信息
     *
     * @param userId
     * @return
     */
    PlatformUserBasicInfoEntity findUserBasicInfo(String userId);
}
