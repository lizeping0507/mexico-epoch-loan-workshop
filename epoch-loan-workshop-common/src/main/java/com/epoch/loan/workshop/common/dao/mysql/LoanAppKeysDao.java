package com.epoch.loan.workshop.common.dao.mysql;

import com.epoch.loan.workshop.common.entity.mysql.LoanAppKeysEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.dao.mysql
 * @className : LoanAppKeyDao
 * @createTime : 2021/11/22 15:42
 * @description : App端所需配置信息
 */
@Mapper
public interface LoanAppKeysDao {

    LoanAppKeysEntity getLoanAppKeyByAppName(String appName);

}
