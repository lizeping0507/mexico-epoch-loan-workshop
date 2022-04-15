package com.epoch.loan.workshop.common.dao.mysql;

import com.epoch.loan.workshop.common.entity.mysql.PlatformUserBasicInfoEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.dao.mysql
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
