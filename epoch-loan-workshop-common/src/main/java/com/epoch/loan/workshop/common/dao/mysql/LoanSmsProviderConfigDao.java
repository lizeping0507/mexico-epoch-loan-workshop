package com.epoch.loan.workshop.common.dao.mysql;

import com.epoch.loan.workshop.common.entity.mysql.LoanSmsProviderConfig;

import java.util.List;

/**
 * @author 魏玉强
 * @packagename : com.epoch.loan.workshop.common.dao.mysql
 * @className : LoanSmsProviderConfigDao
 * @createTime  2022/04/18 19:37
 * @Description:
 */
public interface LoanSmsProviderConfigDao {

    /**
     * 查询 渠道配置
     *
     * @return 渠道配置列表
     */
    List<LoanSmsProviderConfig> findSmsConfig();
}
