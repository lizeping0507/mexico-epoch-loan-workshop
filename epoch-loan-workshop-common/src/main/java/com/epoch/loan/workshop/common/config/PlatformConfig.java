package com.epoch.loan.workshop.common.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.config
 * @className : PlatformConfig
 * @createTime : 2022/3/21 17:56
 * @description : TODO 一句话描述该类的功能
 */
@RefreshScope
@Configuration
@Data
public class PlatformConfig {
    /**
     *
     */
    @Value("${platform.domain}")
    private String platformDomain;
}
