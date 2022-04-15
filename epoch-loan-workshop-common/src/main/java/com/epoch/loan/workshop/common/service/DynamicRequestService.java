package com.epoch.loan.workshop.common.service;

import com.epoch.loan.workshop.common.entity.mysql.LoanDynamicRequestEntity;

import java.util.List;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.service;
 * @className : DynamicRequestService
 * @createTime : 2022/4/1 17:43
 * @description : 动态映射相关业务接口
 */
public interface DynamicRequestService {

    List<LoanDynamicRequestEntity> findAll();
}
