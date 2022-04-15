package com.epoch.loan.workshop.common.dao.mysql;

import com.epoch.loan.workshop.common.entity.mysql.PlatformUserOcrBasicInfoEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.dao.mysql
 * @className : PlatformUserOcrBasicInfoDao
 * @createTime : 2021/11/19 16:37
 * @description : 用户Ocr认证信息 TODO 老表
 */
@Mapper
public interface PlatformUserOcrBasicInfoDao {

    /**
     * 查询用户Ocr认证信息
     *
     * @param userId
     * @return
     */
    PlatformUserOcrBasicInfoEntity findUserOcrBasicInfo(String userId);
}
