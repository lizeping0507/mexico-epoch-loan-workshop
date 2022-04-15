package com.epoch.loan.workshop.common.dao.mysql;

import com.epoch.loan.workshop.common.entity.mysql.LoanDynamicRequestEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.dao.mysql
 * @className : LoanDynamicRequestDao
 * @createTime : 2021/11/22 15:42
 * @description : 动态请求配置表
 */
@Mapper
public interface LoanDynamicRequestDao {
    /**
     * 查询所有
     *
     * @return 所有配置
     */
    List<LoanDynamicRequestEntity> findAll();
}
