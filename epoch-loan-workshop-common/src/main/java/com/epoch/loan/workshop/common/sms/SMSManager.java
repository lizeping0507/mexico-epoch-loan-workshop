package com.epoch.loan.workshop.common.sms;

import com.epoch.loan.workshop.common.constant.SMSChannelConfigStatus;
import com.epoch.loan.workshop.common.dao.mysql.LoanSMSChannelConfigDao;
import com.epoch.loan.workshop.common.entity.mysql.LoanSMSChannelConfigEntity;
import com.epoch.loan.workshop.common.sms.channel.SMSChannel;
import com.epoch.loan.workshop.common.sms.channel.Situation;
import com.epoch.loan.workshop.common.util.LogUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Random;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.sms
 * @className : SMSManager
 * @createTime : 2022/4/20 14:11
 * @description : 短信管理器
 */
@Component
public class SMSManager {

    /**
     * 短信渠道
     */
    @Autowired
    private LoanSMSChannelConfigDao loanSMSChannelConfigDao;

    /**
     * 发送短信验证码
     *
     * @param mobile 手机号
     * @return 验证码
     */
    public String sendVerificationCode(String mobile) throws Exception {
        // 获取验证码
        String code = generateVerificationCode();

        // 发送短信验证码
        Situation situation = smsChannel().sendVerificationCode(code,
                "0052" + mobile);
        if (!situation.isResult()) {
            LogUtil.sysError("[SMSManager sendVerificationCode]"+ situation.getResponse());
            return null;
        }

        return code;
    }

    /**
     * 短信渠道
     *
     * @return
     * @throws Exception
     */
    protected SMSChannel smsChannel() throws Exception {
        // 获取短信渠道
        LoanSMSChannelConfigEntity loanSMSChannelConfigEntity = getSMSChannel();
        if (ObjectUtils.isEmpty(loanSMSChannelConfigEntity)) {
            throw new Exception();
        }

        // 通过反射获取对象
        Class clz = Class.forName(loanSMSChannelConfigEntity.getReflex());
        Constructor constructor = clz.getConstructor(LoanSMSChannelConfigEntity.class);

        // 获取短信对象并注入接口
        SMSChannel smsChannel = (SMSChannel) constructor.newInstance(loanSMSChannelConfigEntity);
        return smsChannel;
    }

    /**
     * 获取短信渠道
     *
     * @return
     */
    protected LoanSMSChannelConfigEntity getSMSChannel() {
        // 获取短信渠道
        List<LoanSMSChannelConfigEntity> smsChannelConfigEntityList = loanSMSChannelConfigDao.findSMSChannelConfigListByStatus(SMSChannelConfigStatus.START);
        if (CollectionUtils.isEmpty(smsChannelConfigEntityList)) {
            return null;
        }

        // 使用权重筛选渠道
        return chooseByWeight(smsChannelConfigEntityList);
    }

    /**
     * 生成短信验证码
     *
     * @return
     */
    protected String generateVerificationCode() {
        long codeL = System.nanoTime();
        String codeStr = Long.toString(codeL);
        return codeStr.substring(codeStr.length() - 8, codeStr.length() - 2);
    }

    /**
     * 根据权重 递归 挑选渠道
     *
     * @param smsChannelConfigEntityList 渠道权重列表
     * @return 渠道配置
     */
    private LoanSMSChannelConfigEntity chooseByWeight(List<LoanSMSChannelConfigEntity> smsChannelConfigEntityList) {
        LoanSMSChannelConfigEntity res = null;

        // 查询渠道列表
        // 随机范围 = 渠道权重和
        int range = smsChannelConfigEntityList.stream().mapToInt(LoanSMSChannelConfigEntity::getProportion).sum();
        if (range == 0) {
            return null;
        }

        // 取随机数
        Random random = new Random();
        int randomNum = random.nextInt(range) + 1;

        // 选择渠道
        int start = 0;
        for (LoanSMSChannelConfigEntity entity : smsChannelConfigEntityList) {
            Integer proportion = entity.getProportion();
            if (randomNum > start && randomNum <= (start + proportion)) {
                res = entity;
                break;
            } else {
                start += proportion;
            }
        }

        return res;
    }

}
