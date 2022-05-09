package com.epoch.loan.workshop.common.config;

import com.epoch.loan.workshop.common.util.LogUtil;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Created with IntelliJ IDEA 2020.1
 *
 * @author 魏玉强
 * @version 1.0
 * @date 2022-03-11 11:24
 * @Description: minio配置
 * @program: epoch-loan-workshop
 * @packagename: com.epoch.loan.workshop.common.config
 */
@RefreshScope
@Configuration
@Data
public class MinioConfig {

    /**
     * 对象存储服务的UR
     */
    @Value("${minio.endpoint}")
    private String endpoint;

    /**
     * 端口
     */
    @Value("${minio.port}")
    private int port;

    /**
     * 如果是true，则用的是https而不是http,默认值是true
     */
    @Value("${minio.useSSL}")
    private Boolean useSSL;

    /**
     * 用户名
     */
    @Value("${minio.accessKey}")
    private String accessKey;

    /**
     * 密码
     */
    @Value("${minio.secretKey}")
    private String secretKey;

    /**
     * 桶名
     */
    @Value("${react.oss.bucketName.user}")
    public String bucketName;

    @Bean
    @Primary
    public MinioClient minioClient() {

        MinioClient minioClient = null;

        try {

            // 创建minioClient
            minioClient = MinioClient.builder().
                    endpoint(endpoint, port, useSSL).credentials(accessKey, secretKey).build();

            // 判断是否存在桶
            boolean hasBucket = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());

            //不存在则创建桶
            if (!hasBucket) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
        } catch (Exception e) {
            LogUtil.sysError("[init minioClient exception]", e);
        }
        return minioClient;
    }
}
