package com.epoch.loan.workshop.common.dao.mysql;

import com.epoch.loan.workshop.common.entity.mysql.LoanAppConfigEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author 魏玉强
 * @packagename : com.epoch.loan.workshop.common.dao.mysql
 * @className : PlatformAppConfigDao
 * @createTime : 2022/07/18 17:08
 * @Description:
 */
@Mapper
public interface LoanAppConfigDao {

    /**
     * 根据appId获取app相关配置
     *
     * @param appName app包名
     * @return app相关配置集合
     */
    LoanAppConfigEntity findByAppName(String appName);
}
