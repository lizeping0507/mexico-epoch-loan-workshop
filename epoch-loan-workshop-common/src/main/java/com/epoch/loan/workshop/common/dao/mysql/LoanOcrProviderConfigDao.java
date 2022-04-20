package com.epoch.loan.workshop.common.dao.mysql;

import com.epoch.loan.workshop.common.entity.mysql.LoanOcrProviderConfig;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author 魏玉强
 * @packagename : com.epoch.loan.workshop.common.entity.mysql
 * @className : LoanOcrProviderConfigDao
 * @createTime : 2022/04/18 15:18
 * @description : 三方聚道配置
 */
@Mapper
public interface LoanOcrProviderConfigDao {

    /**
     * 查询 渠道配置
     *
     * @param appName app标识
     * @return 渠道配置列表
     */
    List<LoanOcrProviderConfig> findProviderConfig(String appName);

    /**
     * 查询 advance配置
     *
     * @param appName app标识
     * @return advance JSON配置字符串
     */
    String selectAdvanceConfig(String appName);
}
