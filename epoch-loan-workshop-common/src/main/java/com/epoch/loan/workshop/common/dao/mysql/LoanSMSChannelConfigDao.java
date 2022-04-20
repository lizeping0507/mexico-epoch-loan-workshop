package com.epoch.loan.workshop.common.dao.mysql;

import com.epoch.loan.workshop.common.entity.mysql.LoanSMSChannelConfigEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.dao.mysql
 * @className : LoanSMSChannelConfigDao
 * @createTime : 2022/4/20 14:40
 * @description : 短信渠道
 */
@Mapper
public interface LoanSMSChannelConfigDao {
    /**
     * 查询短信渠道列表
     *
     * @param status
     * @return
     */
    List<LoanSMSChannelConfigEntity> findSMSChannelConfigListByStatus(Integer status);
}
