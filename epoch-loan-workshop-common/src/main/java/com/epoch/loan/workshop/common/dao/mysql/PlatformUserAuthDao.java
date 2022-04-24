package com.epoch.loan.workshop.common.dao.mysql;

import com.epoch.loan.workshop.common.entity.mysql.PlatformUserAuthEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PlatformUserAuthDao {
    /**
     * 查询用户认证状况详情
     *
     * @param id
     * @return
     */
    PlatformUserAuthEntity findUserAuth(String id);
}
