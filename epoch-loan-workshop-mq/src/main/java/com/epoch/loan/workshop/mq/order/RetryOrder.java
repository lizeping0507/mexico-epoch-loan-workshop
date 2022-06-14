package com.epoch.loan.workshop.mq.order;

import com.epoch.loan.workshop.common.constant.OrderStatus;
import com.epoch.loan.workshop.common.dao.mysql.LoanOrderDao;
import com.epoch.loan.workshop.common.dao.mysql.LoanOrderModelDao;
import com.epoch.loan.workshop.common.entity.mysql.LoanOrderEntity;
import com.epoch.loan.workshop.common.mq.order.OrderMQManager;
import com.epoch.loan.workshop.common.mq.order.params.OrderParams;
import com.epoch.loan.workshop.common.util.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 进行中订单重入队列
 */
@Component
public class RetryOrder {
    @Autowired
    LoanOrderDao loanOrderDao;
    @Autowired
    LoanOrderModelDao loanOrderModelDao;
    @Autowired
    OrderMQManager orderMQManager;

    public void retry() {
        // 查询 审核中 审核中 放款中订单
        int[] statues = {OrderStatus.EXAMINE_WAIT, OrderStatus.WAIT_PAY, OrderStatus.EXAMINE_PASS};
        List<LoanOrderEntity> orderList = loanOrderDao.findOrderByStatusIn(statues);

        LogUtil.sysInfo("RetryOrder start : {}",orderList.size());

        // 重入队列
        orderList.forEach(x -> {
            try {
                List<String> modelList = loanOrderModelDao.findNamesByGroup(x.getOrderModelGroup());
                OrderParams orderParams = new OrderParams();
                orderParams.setOrderId(x.getId());
                orderMQManager.sendMessage(orderParams, modelList.get(0));
            } catch (Exception e) {
                LogUtil.sysError("[RetryOrder "+ x.getId() +"]" ,e);
            }
        });
        LogUtil.sysInfo("RetryOrder end");
    }
}
