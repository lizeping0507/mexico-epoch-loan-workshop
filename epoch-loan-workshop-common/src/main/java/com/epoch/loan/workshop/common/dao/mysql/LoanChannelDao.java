package com.epoch.loan.workshop.common.dao.mysql;

import com.epoch.loan.workshop.common.entity.mysql.LoanChannelEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.dao.mysql
 * @className : PlatformChannelDao
 * @createTime : 2021/12/6 18:22
 * @description : 渠道
 */
@Mapper
public interface LoanChannelDao {

    /**
     * 插入新聚道
     *
     * @param loanChannelEntity 聚道信息
     */
    void insert(@Param("loanChannelEntity") LoanChannelEntity loanChannelEntity);

    /**
     * 根据ID查询渠道
     *
     * @param id 渠道ID
     * @return 渠道信息
     */
    LoanChannelEntity findChannel(Integer id);

    /**
     * 根据聚道code码 查询渠道
     *
     * @param channelCode 聚道code码
     * @return 渠道信息
     */
    LoanChannelEntity findByChannelCode(String channelCode);

}
