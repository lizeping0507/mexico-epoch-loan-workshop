package com.epoch.loan.workshop.common.dao.mysql;

import com.epoch.loan.workshop.common.entity.mysql.LoanUserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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
     * @param id
     * @param newPassword
     */
    void updatePassword(String id, String newPassword);

    /**
     * 通过Id查询
     * @param id
     * @return
     */
    LoanUserEntity findById(String id);
}
