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
 * @className : SdkCatchDataSyncLogElasticEntity
 * @createTime : 2022/04/26 17:10
 * @Description:  epoch抓取数据同步回调日志
 */
@Data
@Document(indexName = "sdk_catch_data_sync_log", shards = 3, replicas = 1)
public class SdkCatchDataSyncLogElasticEntity {

    /**
     * id
     */
    @Id
    private String id;

    /**
     * 用户id
     */
    @Field(type = FieldType.Keyword)
    private String userId;

    /**
     * 贷超订单编号
     */
    @Field(type = FieldType.Keyword)
    private String orderNo;

    /**
     * 报告抓取状态1：成功 2 成功失败
     */
    @Field(type = FieldType.Keyword)
    private Integer reportStatus;

    /**
     * epoch抓取原始返回码
     */
    @Field(type = FieldType.Keyword)
    private String code;

    /**
     * epoch抓取原始数据返回信息
     */
    @Field(type = FieldType.Text)
    private String message;

    /**
     * 抓取类型：msg：短信 app: app img：相册 contact：通讯录 device：设备信息
     */
    @Field(type = FieldType.Keyword)
    private String type;

    /**
     * 创建时间
     */
    @Field(type = FieldType.Keyword)
    private Date createTime;
}
