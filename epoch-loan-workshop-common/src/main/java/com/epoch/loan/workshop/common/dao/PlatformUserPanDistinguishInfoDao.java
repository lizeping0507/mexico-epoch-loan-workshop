package com.epoch.loan.workshop.common.dao;

import com.epoch.loan.workshop.common.entity.PlatformUserPanDistinguishInfoEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.dao
 * @className : PlatformUserPanDistinguishInfoDao
 * @createTime : 2021/11/19 18:32
 * @description : Pan卡识别信息表 TODO 老表
 */
@Mapper
public interface PlatformUserPanDistinguishInfoDao {

    /**
     * 查询用户 Pan卡识别信息表
     * @param userId
     * @return
     */
    PlatformUserPanDistinguishInfoEntity findUserPanDistinguishInfo(String userId);
}
