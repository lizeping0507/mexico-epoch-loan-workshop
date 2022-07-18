package com.epoch.loan.workshop.common.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.config
 * @className : ReactConfig
 * @createTime : 2021/12/16 19:40
 * @description : 启动配置
 */
@RefreshScope
@Configuration
@Data
public class ReactConfig {

    /**
     * 提还推送地址
     */
    @Value("${react.remindUrl}")
    private String remindUrl;

    /**
     * 催收推送地址
     */
    @Value("${react.reactUrl}")
    private String reactUrl;

    /**
     * minio存储用户 照片访问原始域名
     */
    @Value("${react.imageUrl}")
    private String imageUrl;

}
