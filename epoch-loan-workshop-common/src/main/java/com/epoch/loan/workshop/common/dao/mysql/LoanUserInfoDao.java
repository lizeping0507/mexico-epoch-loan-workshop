package com.epoch.loan.workshop.common.dao.mysql;

import com.epoch.loan.workshop.common.entity.mysql.LoanUserInfoEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.dao.mysql
 * @className : LoanUserInfoDao
 * @createTime : 2022/4/18 14:56
 * @description : 用户详情
 */
@Mapper
public interface LoanUserInfoDao {
    /**
     * 插入新记录
     *
     * @param loanUserInfoEntity
     */
    void insert(@Param("loanUserInfoEntity") LoanUserInfoEntity loanUserInfoEntity);

    /**
     * 查询用户详细信息
     *
     * @param userId 用户id
     * @return
     */
    LoanUserInfoEntity findUserInfoById(String userId);

    /**
     * 更新
     *
     * @param loanUserInfoEntity
     */
    void update(@Param("loanUserInfoEntity") LoanUserInfoEntity loanUserInfoEntity);

    /**
     * 根据 INE证件id 查询用户最近的详细信息
     *
     * @param papersId INE证件id
     * @return 用户详细信息
     */
    LoanUserInfoEntity findLastUserInfoByPapersId(String papersId);

    /**
     * 根据 INE证件id和 app标识 查询用户详细信息
     *
     * @param papersId INE证件id
     * @param appName app标识
     * @return 用户详细信息
     */
    LoanUserInfoEntity findUserInfoByPapersIdAndAppName(String papersId,String appName);

    /**
     * 根据 rfc 和 app标识 查询用户详细信息
     *
     * @param rfc rfc
     * @return 用户详细信息
     */
    LoanUserInfoEntity findLastUserInfoLastByRfc(String rfc);

    /**
     * 根据 rfc 查询用户最近的详细信息
     *
     * @param rfc rfc
     * @param appName app标识
     * @return 用户详细信息
     */
    LoanUserInfoEntity findUserInfoByRfcAndAppName(String rfc,String appName);

    /**
     * 根据 手机号 查询用户最近的详细信息
     *
     * @param mobile 手机号
     * @return 用户详细信息
     */
    LoanUserInfoEntity findLastUserInfoByMobile(String mobile);

    /**
     * 更新gps信息
     *
     * @param id
     * @param gps
     * @param gpsAddress
     */
    void updateUserGpsMsg(String id, String gps, String gpsAddress, Date updateTime);

    /**
     * 查询手机号对应的所有包UserId
     * @param mobile
     * @return
     */
    List<String> findUserIdByMobile(String mobile);
}
