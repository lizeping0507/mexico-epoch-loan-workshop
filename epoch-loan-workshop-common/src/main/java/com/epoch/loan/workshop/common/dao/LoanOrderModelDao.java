package com.epoch.loan.workshop.common.dao;

import com.epoch.loan.workshop.common.entity.LoanOrderModelEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.dao
 * @className : LoanMaskDao
 * @createTime : 2021/11/22 15:42
 * @description : 变身包产品配置
 */
@Mapper
public interface LoanOrderModelDao {
    /**
     * 根据group 查询model名称列表
     *
     * @param groupName
     * @return
     */
    List<String> findNamesByGroup(String groupName);

    /**
     * 根据组名模型名称查询模型信息
     *
     * @param groupName
     * @param modelName
     * @return
     */
    LoanOrderModelEntity findModelByGroupAndModelName(String groupName, String modelName);
}
