package com.epoch.loan.workshop.common.dao.mysql;

import com.epoch.loan.workshop.common.entity.mysql.PlatformUserOcrAadharFrontLogEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.dao.mysql
 * @className : PlatformUserOcrAadharFrontLog
 * @createTime : 2021/11/19 17:54
 * @description : 用户OCR识别aadhar正面日志 TODO 老表
 */
@Mapper
public interface PlatformUserOcrAadharFrontLogDao {
    /**
     * 查询用户addhar正面识别日志
     *
     * @param userId
     * @return
     */
    PlatformUserOcrAadharFrontLogEntity findPlatformUserOcrAadharFrontLog(String userId);
}
