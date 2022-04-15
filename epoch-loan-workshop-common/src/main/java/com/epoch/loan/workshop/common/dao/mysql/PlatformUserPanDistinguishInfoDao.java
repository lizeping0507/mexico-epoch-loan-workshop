package com.epoch.loan.workshop.common.dao.mysql;

import com.epoch.loan.workshop.common.entity.mysql.PlatformUserPanDistinguishInfoEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.dao.mysql
 * @className : PlatformUserPanDistinguishInfoDao
 * @createTime : 2021/11/19 18:32
 * @description : Pan卡识别信息表 TODO 老表
 */
@Mapper
public interface PlatformUserPanDistinguishInfoDao {

    /**
     * 查询用户 Pan卡识别信息表
     *
     * @param userId
     * @return
     */
    PlatformUserPanDistinguishInfoEntity findUserPanDistinguishInfo(String userId);
}
