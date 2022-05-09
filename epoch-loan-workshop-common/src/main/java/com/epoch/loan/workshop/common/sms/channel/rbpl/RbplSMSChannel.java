package com.epoch.loan.workshop.common.sms.channel.rbpl;

import com.alibaba.fastjson.JSONObject;
import com.epoch.loan.workshop.common.entity.mysql.LoanSMSChannelConfigEntity;
import com.epoch.loan.workshop.common.sms.channel.SMSChannel;
import com.epoch.loan.workshop.common.sms.channel.Situation;
import com.epoch.loan.workshop.common.util.HttpUtils;
import com.epoch.loan.workshop.common.util.LogUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.sms.channel.rbpl
 * @className : RbplSMSChannel
 * @createTime : 2022/4/20 15:28
 * @description : Rbpl短信渠道
 */
public class RbplSMSChannel implements SMSChannel {
    /**
     * 短信对象
     */
    private LoanSMSChannelConfigEntity loanSMSChannelConfigEntity;

    /**
     * 有参构造函数
     *
     * @param loanSMSChannelConfigEntity
     */
    public RbplSMSChannel(LoanSMSChannelConfigEntity loanSMSChannelConfigEntity) {
        this.loanSMSChannelConfigEntity = loanSMSChannelConfigEntity;
    }

    /**
     * 发送短信验证码
     *
     * @param code
     * @param mobile
     * @return
     * @throws Exception
     */
    @Override
    public Situation sendVerificationCode(String code, String mobile) throws Exception {
        // 发送情况
        Situation situation = new Situation();

        // 解析配置
        JSONObject configJSon = JSONObject.parseObject(loanSMSChannelConfigEntity.getConfig());

        // 获取短信内容
        String verificationCodeContent = configJSon.getString("verificationCodeContent").replace("{code}", code);

        // 访问Key
        String accessKey = configJSon.getString("accessKey");

        // 访问密匙
        String secretKey = configJSon.getString("secretKey");

        // 发送方，senderId
        String from = configJSon.getString("from");

        // 接受号码
        String to = mobile;

        // 回调地址
        String callbackUrl = configJSon.getString("callbackUrl");

        // 请求地址
        String url = configJSon.getString("optUrl");

        // 封装请求参数
        Map<String, String> params = new HashMap<>();
        params.put("message", verificationCodeContent);
        params.put("accessKey", accessKey);
        params.put("secretKey", secretKey);
        params.put("from", from);
        params.put("to", to);
        params.put("callbackUrl", callbackUrl);

        // 发送请求
        String result = HttpUtils.POST(url, JSONObject.toJSONString(params));
        LogUtil.sysInfo("请求发送验证码  请求参数：{} ，响应参数：{}",JSONObject.toJSONString(params), result);

        // 封装结果就
        situation.setRequest(JSONObject.toJSONString(params));
        situation.setResponse(result);

        if (StringUtils.isEmpty(result)) {
            situation.setResult(false);
            return situation;
        }

        // 解析结果就
        JSONObject resultJson = JSONObject.parseObject(result);
        if (resultJson.getInteger("code") != 200) {
            situation.setResult(false);
            return situation;

        }

        // 发送成功
        situation.setResult(true);
        return situation;
    }
}
