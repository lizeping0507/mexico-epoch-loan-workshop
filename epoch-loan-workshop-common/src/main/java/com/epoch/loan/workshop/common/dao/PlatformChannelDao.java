package com.epoch.loan.workshop.common.dao;

import com.epoch.loan.workshop.common.entity.PlatformChannelEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.dao
 * @className : PlatformChannelDao
 * @createTime : 2021/12/6 18:22
 * @description : 渠道
 */
@Mapper
public interface PlatformChannelDao {

    /**
     * 根据ID查询渠道
     *
     * @param id
     * @return
     */
    PlatformChannelEntity findChannel(Integer id);
}
