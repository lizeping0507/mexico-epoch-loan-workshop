package com.epoch.loan.workshop.control.service;

import com.epoch.loan.workshop.common.entity.elastic.AfCallBackLogElasticEntity;
import com.epoch.loan.workshop.common.entity.mysql.LoanChannelEntity;
import com.epoch.loan.workshop.common.entity.mysql.LoanUserEntity;
import com.epoch.loan.workshop.common.params.params.request.AfCallBackParams;
import com.epoch.loan.workshop.common.params.params.result.Result;
import com.epoch.loan.workshop.common.service.AfCallBackService;
import com.epoch.loan.workshop.common.util.DateUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Date;

/**
 * @author 魏玉强
 * @packagename : com.epoch.loan.workshop.control.service
 * @className : AfCallBackServiceImpl
 * @createTime : 2022/07/23 10:50
 * @Description: af 回调业务处理
 */
@DubboService(timeout = 5000)
public class AfCallBackServiceImpl extends BaseService implements AfCallBackService {

    /**
     * af 回调业务处理
     *
     * @param params af回调参数
     * @return 响应结果
     */
    @Override
    public Result afCallBack(@RequestBody AfCallBackParams params) {
        // 响应结果
        Result response = new Result();

        if (ObjectUtils.isEmpty(params)) {
            return response;
        }

        // 处理聚道
        LoanChannelEntity loanChannel = null;
        if (StringUtils.isNotBlank(params.getMediaSource())) {
            loanChannel = loanChannelDao.findByChannelCode(params.getMediaSource());
            if (ObjectUtils.isEmpty(loanChannel)) {
                LoanChannelEntity channelNew = new LoanChannelEntity();
                channelNew.setChannelCode(params.getMediaSource());
                channelNew.setChannelName(params.getMediaSource());
                channelNew.setCreateTime(new Date());
                channelNew.setIsOpen(true);
                loanChannelDao.insert(channelNew);
                loanChannel = loanChannelDao.findByChannelCode(params.getMediaSource());
            }
        }

        // 根据GaId 查询用户
        LoanUserEntity loanUserEntity = null;
        if (StringUtils.isNotBlank(params.getAdvertisingId())) {
            loanUserEntity = loanUserDao.findByGaId(params.getAdvertisingId());
        }

        if (ObjectUtils.isEmpty(loanChannel) || ObjectUtils.isEmpty(loanUserEntity)) {
            return response;
        }

        // 更新用户的 afId
        loanUserDao.updateAfId(loanUserEntity.getId(), params.getAppsflyerId(), new Date());

        // 更新用户聚道 和 订单聚道
        loanUserDao.updateChannelId(loanUserEntity.getId(), loanChannel.getId(), new Date());

        // 更新订单聚道信息
        loanOrderDao.updateOrderUserChannelId(loanUserEntity.getId(), loanChannel.getId(), new Date());

        // 保存af 回调记录
        AfCallBackLogElasticEntity afCallbackLogElasticEntity = new AfCallBackLogElasticEntity();
        BeanUtils.copyProperties(params, afCallbackLogElasticEntity);
        afCallbackLogElasticEntity.setUserId(loanUserEntity.getId());
        afCallbackLogElasticEntity.setCreateTime(new Date());
        if (StringUtils.isNotBlank(params.getEventTime())) {
            Date eventTime = DateUtil.StringToDate(params.getEventTime(), "yyyy-MM-dd HH:ss:mm.SSS");
            afCallbackLogElasticEntity.setEventTime(eventTime);
        }

        afCallBackLogElasticDao.save(afCallbackLogElasticEntity);
        return response;
    }
}
