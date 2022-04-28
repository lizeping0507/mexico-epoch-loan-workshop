package com.epoch.loan.workshop.common.oss;

import com.aliyun.oss.*;
import com.aliyun.oss.model.PutObjectResult;
import lombok.Data;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.Date;

/**
 * @author 魏玉强
 * @packagename : com.epoch.loan.workshop.common.util
 * @className : OssClient
 * @createTime : 2022/04/22 14:52
 * @Description: 阿里云OSS操作工具类
 */
@RefreshScope
@Component
@Data
public class AlibabaOssClient {

    /**
     * OSS连接池
     */
    @Autowired
    public OSSClient ossClient;

    /**
     * 文件上传
     *
     * @param bucketName 桶名
     * @param objectName 上传路径
     * @param imageData  文件二进制
     * @return 上传是否成功
     */
    public Boolean upload(String bucketName, String objectName, byte[] imageData) {

        // 上传文件。
        PutObjectResult putObjectResult = ossClient.putObject(bucketName, objectName, new ByteArrayInputStream(imageData));

        // 上传成功response为空，上传失败,OOS服务端会响应状态码和错误信息
        if (ObjectUtils.isEmpty(putObjectResult.getResponse())) {
            return true;
        }

        return false;
    }

    /**
     * 获取文件临时链接
     *
     * @param bucketName     桶名
     * @param objectName     上传路径
     * @param dateExpiration 过期时间
     * @return 预签
     */
    public String getFileUrl(String bucketName, String objectName, Date dateExpiration) {
        // 设置签名URL过期时间
        if (ObjectUtils.isEmpty(dateExpiration)) {

            // 指定过期时间为10分钟。
            dateExpiration = new Date(System.currentTimeMillis() + 1000 * 60 * 10);
        }

        // 生成以GET方法访问的签名URL，访客可以直接通过浏览器访问相关内容。
        URL url = ossClient.generatePresignedUrl(bucketName, objectName, dateExpiration, HttpMethod.GET);

        if (ObjectUtils.isNotEmpty(url)) {

            // 原始路径为 ip:port/bucketName/objectName?OSS自带参数，
            // 截取后只保留 ip:port/bucketName/objectName
            return url.toString().split("\\?")[0];
        }

        return null;
    }

    /**
     * 文件上传并获取临时链接
     *
     * @param bucketName     桶名
     * @param objectName     上传路径
     * @param imageData      文件二进制
     * @param dateExpiration 过期时间
     * @return 预签
     */
    public String upload(String bucketName, String objectName, byte[] imageData, Date dateExpiration) {
        // 上传文件
        boolean upload = upload(bucketName, objectName, imageData);
        if (!upload) {
            return null;
        }

        // 获取文件访问地址
        return getFileUrl(bucketName, objectName, dateExpiration);
    }
}
