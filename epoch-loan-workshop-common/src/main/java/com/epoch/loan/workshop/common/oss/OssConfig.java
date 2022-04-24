package com.epoch.loan.workshop.common.oss;

import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.comm.Protocol;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * @author 魏玉强
 * @packagename : com.epoch.loan.workshop.common.config
 * @className : OssConfig
 * @createTime : 2022/03/28 15:23
 * @Description: 阿里云服务器配置
 */
@RefreshScope
//@Configuration TODO
@Data
public class OssConfig{

    /**
     * 访问域名
     */
    @Value("${oss.endpoint}")
    private String endpoint;

    /**
     * 访问标识用户
     */
    @Value("${oss.accessKeyId}")
    private String accessKeyId;

    /**
     * 访问签名密钥
     */
    @Value("${oss.accessKeySecret}")
    private String accessKeySecret;

    @Bean
    @Primary
    public OSS getOSSClient() {
        ClientBuilderConfiguration conf = new ClientBuilderConfiguration();

        // 设置OSSClient允许打开的最大HTTP连接数，默认为1024个。
        conf.setMaxConnections(1024);

        // 设置Socket层传输数据的超时时间，默认为50000毫秒。
        conf.setSocketTimeout(50000);

        // 设置建立连接的超时时间，默认为50000毫秒。
        conf.setConnectionTimeout(50000);

        // 设置从连接池中获取连接的超时时间（单位：毫秒），默认不超时。
        conf.setConnectionRequestTimeout(1000);

        // 设置连接空闲超时时间。超时则关闭连接，默认为60000毫秒。
        conf.setIdleConnectionTime(10000);

        // 设置失败请求重试次数，默认为3次。
        conf.setMaxErrorRetry(5);

        // 设置是否支持将自定义域名作为Endpoint，默认支持。
        conf.setSupportCname(false);

        // 设置是否开启二级域名的访问方式，默认不开启。
        conf.setSLDEnabled(true);

        // 设置连接OSS所使用的协议（HTTP/HTTPS），默认为HTTP。
        conf.setProtocol(Protocol.HTTP);

        // 设置用户代理，指HTTP的User-Agent头，默认为aliyun-sdk-java。
        conf.setUserAgent("aliyun-sdk-java");

        // 设置是否开启HTTP重定向，默认开启。
        conf.setRedirectEnable(true);

        // 设置是否开启SSL证书校验，默认开启。
        conf.setVerifySSLEnable(true);

        // 创建OSSClient实例
        return new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret, conf);
    }
}
