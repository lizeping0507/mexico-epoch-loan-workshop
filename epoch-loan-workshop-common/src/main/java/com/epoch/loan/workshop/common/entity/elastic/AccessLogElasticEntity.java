package com.epoch.loan.workshop.common.entity.elastic;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.entity.elastic
 * @className : TestBean
 * @createTime : 2022/3/28 18:35
 * @description : 响应日志elastic实体类
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
@Document(indexName = "mexico_access_log", type = "_doc", shards = 3, replicas = 1)
public class AccessLogElasticEntity {
    /**
     * 响应流水号
     */
    @Id
    private String serialNo;

    /**
     * 请求开始时间
     */
    @Field(type = FieldType.Long)
    private Long requestTime;

    /**
     * 响应时间
     */
    @Field(type = FieldType.Date)
    private Long responseTime;

    /**
     * 请求地址
     */
    @Field(type = FieldType.Keyword)
    private String url;

    /**
     * 映射地址
     */
    @Field(type = FieldType.Keyword)
    private String mappingUrl;

    /**
     * 请求ip
     */
    @Field(type = FieldType.Keyword)
    private String ip;

    /**
     * 应用名称
     */
    @Field(type = FieldType.Keyword)
    private String applicationName;

    /**
     * 当前服务地址
     */
    @Field(type = FieldType.Keyword)
    private String serverIp;

    /**
     * 当前服务端口
     */
    @Field(type = FieldType.Keyword)
    private String port;

    /**
     * 响应数据
     */
    @Field(type = FieldType.Text)
    private String response;

    /**
     * 请求数据
     */
    @Field(type = FieldType.Text)
    private String request;

    /**
     * 访问耗时时间(MS)
     */
    @Field(type = FieldType.Long)
    private Long accessSpend;

    /**
     * 异常信息
     */
    @Field(type = FieldType.Text)
    private String ex;
}
