package com.epoch.loan.workshop.common.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.config
 * @className : RiskConfig
 * @createTime : 2021/11/18 15:0
 * @description : 风控配置
 */
@RefreshScope
@Configuration
@Data
public class RiskConfig {

    /**
     * AppId
     */
    @Value("${risk.appId}")
    public String appId;

    /**
     * 密钥
     */
    @Value("${risk.privateKey}")
    public String privateKey;

    /**
     * 风控请求地址
     */
    @Value("${risk.riskUrl}")
    public String riskUrl;

    /**
     * 风控请求地址
     */
    @Value("${risk.cloudPushUrl}")
    public String cloudPushUrl;

    /**
     * 风控请求地址
     */
    @Value("${risk.cloudPayUrl}")
    public String cloudPayUrl;

    /**
     * 风控请求地址
     */
    @Value("${risk.cloudPaySuccessUrl}")
    public String cloudPaySuccessUrl;

    /**
     * 风控请求地址
     */
    @Value("${risk.cloudPayStatusUrl}")
    public String cloudPayStatusUrl;

    /**
     * 风控CURP校验请求地址
     */
    @Value("${risk.cloudCheckCURPUrl}")
    public String cloudCheckCURPUrl;

    /**
     * 贷超公钥
     */
    @Value("${risk.loanPrivateKey}")
    public String loanPrivateKey;
}
