package com.epoch.loan.workshop.common.dao.mysql;

import com.epoch.loan.workshop.common.entity.mysql.PlatformUserPersonalInfoEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.dao.mysql
 * @className : PlatformUserPersonalInfoDao
 * @createTime : 2021/11/25 11:07
 * @description : 用户个人信息
 */
@Mapper
public interface PlatformUserPersonalInfoDao {

    /**
     * 查询用户个人信息
     *
     * @param userId
     * @return
     */
    PlatformUserPersonalInfoEntity findUserPersonalInfo(String userId);
}
