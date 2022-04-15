package com.epoch.loan.workshop.common.dao.mysql;

import com.epoch.loan.workshop.common.entity.mysql.PlatformUserAadharDistinguishInfoEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.dao.mysql
 * @className : PlatformUserAadharDistinguishInfoDao
 * @createTime : 2021/11/19 17:43
 * @description : 用户aadhar卡识别信息 TODO 老表
 */
@Mapper
public interface PlatformUserAadharDistinguishInfoDao {
    /**
     * 查询用户aadhar卡识别信息
     *
     * @param userId
     * @return
     */
    PlatformUserAadharDistinguishInfoEntity findUserAadharDistinguishInfo(String userId);
}
