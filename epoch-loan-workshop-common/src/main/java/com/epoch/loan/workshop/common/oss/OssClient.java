package com.epoch.loan.workshop.common.oss;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.PutObjectRequest;
import com.epoch.loan.workshop.common.util.LogUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URL;
import java.util.Date;

/**
 * @author 魏玉强
 * @packagename : com.epoch.loan.workshop.common.util
 * @className : OssClient
 * @createTime : 2022/04/22 14:52
 * @Description: 阿里云OSS操作工具类
 */
@Component
public class OssClient implements InitializingBean {

    @Autowired
    private OSS ossClient;

    private static OSS ossClientStatic;

    /**
     * 文件上传
     *
     * @param bucketName 桶名
     * @param objectName 上传路径
     * @param file       文件
     * @return
     */
    public static Boolean uploadFile(String bucketName, String objectName, File file) {
        boolean result = false;
        try {

            // 创建PutObjectRequest对象。
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, file);

            // 上传文件。
            ossClientStatic.putObject(putObjectRequest);

            result = true;
        } catch (OSSException oe) {
            LogUtil.sysError("uploadFile oss上传出错", oe);
        } catch (ClientException ce) {
            LogUtil.sysError("uploadFile oss通信出错", ce);
        }
        return result;
    }

    /**
     * 获取文件临时链接
     *
     * @param bucketName     桶名
     * @param objectName     上传路径
     * @param dateExpiration 过期时间
     * @return 预签
     */
    public static String getFileUrl(String bucketName, String objectName, Date dateExpiration) {
        String fileUrl = null;
        URL signedUrl = null;
        try {

            // 设置签名URL过期时间
            if (ObjectUtils.isEmpty(dateExpiration)) {

                // 指定过期时间为10分钟。
                dateExpiration = new Date(System.currentTimeMillis() + 1000 * 60 * 10);
            }

            // 生成以GET方法访问的签名URL，访客可以直接通过浏览器访问相关内容。
            signedUrl = ossClientStatic.generatePresignedUrl(bucketName, objectName, dateExpiration, HttpMethod.GET);
        } catch (OSSException oe) {
            LogUtil.sysError("uploadFile oss上传出错", oe);
        } catch (ClientException ce) {
            LogUtil.sysError("uploadFile oss通信出错", ce);
        }

        if (ObjectUtils.isNotEmpty(signedUrl)) {
            fileUrl = signedUrl.getFile();
        }
        return fileUrl;
    }

    /**
     * 文件上传并获取临时链接
     *
     * @param bucketName     桶名
     * @param objectName     上传路径
     * @param file           文件
     * @param dateExpiration 过期时间
     * @return 预签
     */
    public static String uploadFileAndGetUrl(String bucketName, String objectName, File file, Date dateExpiration) {
        String fileUrl = null;
        try {

            // 创建PutObjectRequest对象。
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, file);

            // 上传文件。
            ossClientStatic.putObject(putObjectRequest);


            // 设置签名URL过期时间
            if (ObjectUtils.isEmpty(dateExpiration)) {

                // 指定过期时间为10分钟。
                dateExpiration = new Date(System.currentTimeMillis() + 3600 * 1000);
            }

            // 生成以GET方法访问的签名URL，访客可以直接通过浏览器访问相关内容。
            URL signedUrl = ossClientStatic.generatePresignedUrl(bucketName, objectName, dateExpiration, HttpMethod.GET);

            if (ObjectUtils.isNotEmpty(signedUrl)) {
                fileUrl = signedUrl.getFile();
            }
        } catch (OSSException oe) {
            LogUtil.sysError("uploadFile oss上传出错", oe);
        } catch (ClientException ce) {
            LogUtil.sysError("uploadFile oss通信出错", ce);
        }
        return fileUrl;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        OssClient.ossClientStatic = this.ossClient;
    }
}
