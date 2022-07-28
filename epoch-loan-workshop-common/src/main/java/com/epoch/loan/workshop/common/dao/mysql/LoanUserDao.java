package com.epoch.loan.workshop.common.dao.mysql;

import com.epoch.loan.workshop.common.entity.mysql.LoanUserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

@Mapper
public interface LoanUserDao {
    /**
     * 根据appName和手机号查询用户是否存在
     *
     * @param appName
     * @param loginName
     * @return
     */
    Integer exitByAppNameAndLoginName(String appName, String loginName);

    /**
     * 通过appName和登录手机号查询用户
     *
     * @param loginName
     * @param appName
     * @return
     */
    LoanUserEntity findByLoginNameAndAppName(String loginName, String appName);

    /**
     * 更新用户app版本号
     *
     * @param id
     * @param appVersion
     */
    void updateAppVersion(String id, String appVersion);

    /**
     * 插入新纪录
     *
     * @param loanUserEntity
     */
    void insert(@Param("loanUserEntity") LoanUserEntity loanUserEntity);

    /**
     * 更新密码
     *
     * @param id
     * @param newPassword
     */
    void updatePassword(String id, String newPassword);

    /**
     * 通过Id查询
     *
     * @param id
     * @return
     */
    LoanUserEntity findById(String id);

    /**
     * 根据设备号和appName查询 是否存在用户
     *
     * @param appName   包名
     * @param androidId 安卓id
     * @return 查询数量
     */
    Integer exitByAppNameAndAndroidId(String appName, String androidId);

    /**
     * 根据afId和appName查询 用户信息
     *
     * @param gaId 用户可重置的设备ID，又称GAID
     * @return 用户信息
     */
    LoanUserEntity findByGaId(String gaId);

    /**
     * 更新afId
     *
     * @param id         用户id
     * @param afId       用户在其设备上安装应用时由SKD生成的非重ID
     * @param updateTime 更新时间
     */
    void updateAfId(String id, String afId, Date updateTime);

    /**
     * 更新用户聚道
     *
     * @param id         用户id
     * @param channelId  聚道id
     * @param updateTime 更新时间
     */
    void updateChannelId(String id, Long channelId, Date updateTime);
}
