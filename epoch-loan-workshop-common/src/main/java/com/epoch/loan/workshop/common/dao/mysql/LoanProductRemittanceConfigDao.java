package com.epoch.loan.workshop.common.dao.mysql;

import com.epoch.loan.workshop.common.entity.mysql.LoanProductRemittanceConfigEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.dao.mysql
 * @className : LoanRemittanceDistributionDao
 * @createTime : 2021/12/18 17:46
 * @description : 放款分配配置
 */
@Mapper
public interface LoanProductRemittanceConfigDao {
    /**
     * 查询放款分配
     *
     * @param groupName
     * @return
     */
    LoanProductRemittanceConfigEntity findByGroupName(String groupName);
}
