package com.epoch.loan.workshop.common.dao.mysql;

import com.epoch.loan.workshop.common.entity.mysql.LoanAppControlEntity;
import com.epoch.loan.workshop.common.entity.mysql.LoanMaskEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.dao.mysql
 * @className : LoanAppControlDao
 * @createTime : 2021/11/22 15:42
 * @description : 版本控制
 */
@Mapper
public interface LoanAppControlDao {

    /**
     * 通过名称和版本号查询版本控制信息
     * @param appName
     * @param appVersion
     * @return
     */
    LoanAppControlEntity findByAppNameAndAppVersion(String appName, String appVersion);
}
