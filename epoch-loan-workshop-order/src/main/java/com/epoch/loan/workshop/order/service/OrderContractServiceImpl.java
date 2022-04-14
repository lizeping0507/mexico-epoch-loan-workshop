package com.epoch.loan.workshop.order.service;

import com.epoch.loan.workshop.common.service.OrderContractService;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.order.service;
 * @className : OrderContractServiceImpl
 * @createTime : 2022/3/19 15:07
 * @description : TODO
 */
@DubboService(timeout = 50000)
public class OrderContractServiceImpl implements OrderContractService {

    @Override
    public String test() {
        return "test()";
    }
}
