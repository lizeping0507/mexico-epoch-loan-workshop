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
     *  用户id
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
     * advance 检测结果
     */
    @Field(type = FieldType.Keyword)
    private String detectionResult;

    /**
     * avacne 活体检测分数
     */
    @Field(type = FieldType.Keyword)
    private String livenessScore;

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
     * advance 付费标识
     */
    @Field(type = FieldType.Keyword)
    private String pricingStrategy;

    /**
     * 创建时间
     */
    @Field(type= FieldType.Date)
    private Date createTime;
}
