package com.epoch.loan.workshop.common.dao.mysql;

import com.epoch.loan.workshop.common.entity.mysql.PlatformUserOcrPanFrontLogEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.dao.mysql
 * @className : PlatformUserOcrPanFrontLogDao
 * @createTime : 2021/11/19 18:23
 * @description : 用户ocr识别pan卡日志 TODO 老表
 */
@Mapper
public interface PlatformUserOcrPanFrontLogDao {
    /**
     * 查询用户ocr识别pan卡日志
     *
     * @param userId
     * @return
     */
    PlatformUserOcrPanFrontLogEntity findUserOcrPanFrontLog(String userId);
}
