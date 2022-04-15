package com.epoch.loan.workshop.common.dao.mysql;


import com.epoch.loan.workshop.common.entity.mysql.LoanTimingEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.dao.mysql
 * @className : LoanTimingDao
 * @createTime : 2021/12/6 18:22
 * @description : 定时任务
 */
@Mapper
public interface LoanTimingDao {
    /**
     * 根据任务类型查询定时任务
     *
     * @param type
     * @return
     */
    List<LoanTimingEntity> findTimingByType(@Param("type") int type);

    /**
     * 根据定时任务名称及类型查询定时任务
     *
     * @param name
     * @param type
     * @return
     */
    LoanTimingEntity findTimingByNameType(@Param("name") String name, @Param("type") int type);

    /**
     * 删除指定名称及任务类型定时任务
     *
     * @param name
     * @param type
     * @return
     */
    boolean removeTiming(@Param("name") String name, @Param("type") int type);


    /**
     * 查询定时任务是否存在
     *
     * @param name
     * @return
     */
    int existTimingByName(@Param("name") String name);

    /**
     * 查询全部定时任务
     *
     * @return
     */
    List<LoanTimingEntity> findTiming();

    /**
     * 查询定时任务配置参数
     *
     * @param name
     * @return
     */
    String findTimingParams(@Param("name") String name);

    /**
     * 增加定时任务
     *
     * @param loanTimingEntity
     * @return
     */
    boolean addTiming(@Param("remindTimingEntity") LoanTimingEntity loanTimingEntity);

    /**
     * 增加定时任务
     *
     * @param loanTimingEntity
     * @return
     */
    boolean updateTiming(@Param("remindTimingEntity") LoanTimingEntity loanTimingEntity);

    /**
     * 更新最后执行时间
     *
     * @param name
     * @param time
     * @return
     */
    boolean updateTimingLastRunTime(@Param("name") String name, @Param("time") Date time);
}