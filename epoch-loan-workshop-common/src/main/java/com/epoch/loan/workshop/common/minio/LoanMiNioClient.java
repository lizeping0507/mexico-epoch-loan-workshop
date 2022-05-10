package com.epoch.loan.workshop.common.minio;

import com.aliyun.oss.model.OSSObject;
import com.epoch.loan.workshop.common.util.LogUtil;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * @author zeping.li
 * @version 1.0
 * @className MiNioUtil
 * @package com.epoch.call.centre.common.util
 * @description minio工具类
 * @since 2021/1/4 15:09
 */
@RefreshScope
@Component
@Data
public class LoanMiNioClient {

    @Autowired
    private MinioClient minioClient;

    /**
     * 上传单个文件到指定的桶,并返回指定链接
     *
     * @param bucketName 桶名称
     * @param objectName 上传的桶内路径
     * @param imgInfo   文件信息
     * @return http链接
     * @throws Exception 桶名不存在、上传文件失败
     */
    public String upload(String bucketName, String objectName, OSSObject imgInfo) throws Exception {

        // 上传
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .stream(imgInfo.getObjectContent(), imgInfo.getObjectMetadata().getContentLength(), -1)
                        .contentType(imgInfo.getObjectMetadata().getContentType())
                        .build());
        return preview(bucketName, objectName);
    }

    /**
     * 预览图片
     *
     * @param bucketName 桶名
     * @param ObjectName 桶内文件路径及名字
     * @return http链接
     */
    public String preview(String bucketName, String ObjectName) {
        try {

            // 查看文件地址
            GetPresignedObjectUrlArgs build = GetPresignedObjectUrlArgs.builder().bucket(bucketName).object(ObjectName).method(Method.GET).build();
            String url = minioClient.getPresignedObjectUrl(build);

            // 原始路径为 ip:port/bucketName/objectName?minio自带参数，
            // 截取后只保留 ip:port/bucketName/objectName
            url = url.split("\\?")[0];
            return url;
        } catch (Exception e) {
            LogUtil.sysError("[minio preview]", e);
        }
        return null;
    }

}
