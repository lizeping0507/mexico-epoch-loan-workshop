package com.epoch.loan.workshop.control.service;

import com.alibaba.fastjson.JSONObject;
import com.epoch.loan.workshop.common.constant.PlatformUrl;
import com.epoch.loan.workshop.common.constant.ResultEnum;
import com.epoch.loan.workshop.common.entity.elastic.SdkCatchDataSyncLogElasticEntity;
import com.epoch.loan.workshop.common.entity.mysql.LoanOrderEntity;
import com.epoch.loan.workshop.common.params.params.request.SdkPushInfoParams;
import com.epoch.loan.workshop.common.params.params.request.SdkUploadParams;
import com.epoch.loan.workshop.common.params.params.result.Result;
import com.epoch.loan.workshop.common.params.params.result.SdkPushInfoResult;
import com.epoch.loan.workshop.common.service.SdkService;
import com.epoch.loan.workshop.common.util.HttpUtils;
import com.epoch.loan.workshop.common.util.PlatformUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : ljy
 * @packageName : com.epoch.loan.workshop.control.service
 * @className : SdkServiceImpl
 * @createTime : 22/3/30 15:26
 * @description : SDK相关
 */
@DubboService(timeout = 5000)
public class SdkServiceImpl extends BaseService implements SdkService {

    /**
     * SDK上传同步回调
     *
     * @param params 入参
     * @return 上传结果
     */
    @Override
    public Result<Object> sdkUploadCallBack(SdkUploadParams params) {
        // 结果集
        Result<Object> result = new Result<>();
        String userId = params.getUser().getId();
        String orderNo = params.getOrderNo();

        // 查询订单是否存在
        LoanOrderEntity orderEntity = loanOrderDao.findOrder(orderNo);
        if (ObjectUtils.isEmpty(orderEntity)) {
            // 封装结果
            result.setReturnCode(ResultEnum.SYSTEM_ERROR.code());
            result.setMessage(ResultEnum.SYSTEM_ERROR.message());
            return result;
        }

        // 日志写入Elastic
        SdkCatchDataSyncLogElasticEntity syncLogElasticEntity = new SdkCatchDataSyncLogElasticEntity();
        BeanUtils.copyProperties(params, syncLogElasticEntity);
        syncLogElasticEntity.setUserId(userId);
        sdkCatchDataSyncLogElasticDao.save(syncLogElasticEntity);

        // 封装结果
        result.setReturnCode(ResultEnum.SUCCESS.code());
        result.setMessage(ResultEnum.SUCCESS.message());
        return result;
    }

}
