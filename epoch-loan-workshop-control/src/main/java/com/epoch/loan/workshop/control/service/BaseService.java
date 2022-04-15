package com.epoch.loan.workshop.control.service;

import com.epoch.loan.workshop.common.config.PlatformConfig;
import com.epoch.loan.workshop.common.dao.mysql.LoanDynamicRequestDao;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.control.service;
 * @className : BaseService
 * @createTime : 2022/3/22 14:28
 * @description : control模块业务基类
 */
public class BaseService {
    /**
     * 动态接口配置
     */
    @Autowired
    public LoanDynamicRequestDao dynamicRequestDao;
    /**
     * 贷超相关配置
     */
    @Autowired
    PlatformConfig platformConfig;
}
