package com.epoch.loan.workshop.common.dao.mysql;

import org.apache.ibatis.annotations.Mapper;

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
}
