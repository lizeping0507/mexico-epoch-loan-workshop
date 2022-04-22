package com.epoch.loan.workshop.common.entity.elastic;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

/**
 * @author 魏玉强
 * @packagename : com.epoch.loan.workshop.common.entity.elastic
 * @className : OcrLivingDetectionLogEntity
 * @createTime : 2022/04/19 20:29
 * @Description: advance活体检测日志
 */
@Data
@Document(indexName = "ocr_living_detection_log", shards = 3, replicas = 1)
public class OcrLivingDetectionLogElasticEntity {

    /**
     * id
     */
    @Id
    private String id;

    /**
     * 请求地址
     */
    @Field(type = FieldType.Keyword)
    private String requestUrl;

    /**
     * 用户id
     */
    @Field(type = FieldType.Keyword)
    private String userId;

    /**
     * advance 响应码
     */
    @Field(type = FieldType.Keyword)
    private String code;

    /**
     * advance 响应描述
     */
    @Field(type = FieldType.Keyword)
    private String message;

    /**
     * 请求参数
     */
    @Field(type = FieldType.Object)
    private Object requestParam;

    /**
     * 请求头
     */
    @Field(type = FieldType.Object)
    private Object requestHeard;

    /**
     * 响应原始数据
     */
    @Field(type = FieldType.Object)
    private Object response;

    /**
     * advacen 额外信息
     */
    @Field(type = FieldType.Keyword)
    private String extra;

    /**
     * advance 唯一标识
     */
    @Field(type = FieldType.Keyword)
    private String transactionId;

    /**
     * advance 付费标识:  FREE, PAY
     */
    @Field(type = FieldType.Keyword)
    private String pricingStrategy;

    /**
     * 创建时间
     */
    @Field(type = FieldType.Date)
    private Date createTime;
}
