package com.epoch.loan.workshop.common.dao.mysql;

import com.epoch.loan.workshop.common.entity.mysql.PlatformUserEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.dao.mysql
 * @className : PlatformUserDao
 * @createTime : 2021/11/19 17:26
 * @description : 用户 TODO 老表
 */
@Mapper
public interface PlatformUserDao {
    /**
     * 查询用户
     *
     * @param userId
     * @return
     */
    PlatformUserEntity findUser(String userId);

    /**
     * 根号用户手机号查询用户信息
     *
     * @param phoneNumber
     * @return
     */
    List<PlatformUserEntity> findUserByPhoneNumber(String phoneNumber);
}
