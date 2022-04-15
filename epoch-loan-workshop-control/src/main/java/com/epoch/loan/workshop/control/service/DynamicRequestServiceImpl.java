package com.epoch.loan.workshop.control.service;

import com.epoch.loan.workshop.common.entity.mysql.LoanDynamicRequestEntity;
import com.epoch.loan.workshop.common.service.DynamicRequestService;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.List;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.control.service;
 * @className : DynamicRequestServiceImpl
 * @createTime : 2022/4/1 17:44
 * @description : TODO
 */
@DubboService(timeout = 5000)
public class DynamicRequestServiceImpl extends BaseService implements DynamicRequestService {

    @Override
    public List<LoanDynamicRequestEntity> findAll() {
        return dynamicRequestDao.findAll();
    }
}
