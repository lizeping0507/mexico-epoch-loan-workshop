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
 * @className : AfCallbackElasticEntity
 * @createTime : 2022/07/23 16:12
 * @Description: af回调日志
 */
@Data
@Document(indexName = "af_call_back_log")
public class AfCallBackLogElasticEntity {

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

    // ---------------------- 设备信息 -----------------------
    /**
     * 用户在其设备上安装应用时由SKD生成的非重ID，
     * 用于在LTV维度对应用内事件进行归因、获取转化数据并关联应用内事件
     * 如果用户对应用进行了卸载重装，SDK会生成新的ID。从iCloud备份中恢复应用不计为新增激活。
     */
    @Field(type = FieldType.Keyword)
    private String appsflyerId;
    /**
     * 用户可重置的设备ID，又称GAID
     */
    @Field(type = FieldType.Keyword)
    private String advertisingId;
    /**
     * 不可重置的永久性设备ID
     */
    @Field(type = FieldType.Keyword)
    private String imei;
    /**
     * 可能出现的值有：phone（电话）、tablet（平板）、other（其他）
     */
    @Field(type = FieldType.Keyword)
    private String deviceCategory;
    /**
     * 设备的商业型号名称
     */
    @Deprecated
    @Field(type = FieldType.Keyword)
    private String deviceType;
    /**
     * 设备的商业型号名称
     */
    @Field(type = FieldType.Keyword)
    private String deviceModel;
    /**
     * 根据SIM MCCMNC识别的移动运营商名称
     */
    @Field(type = FieldType.Keyword)
    private String operator;
    /**
     * 由设备上报的设备操作系统语言（语言环境），通常以ll-DD格式显示。其中ll是语言， DD是方言。例如，en-ZA表示南非地区使用的英语。
     */
    @Field(type = FieldType.Keyword)
    private String language;
    /**
     * 设备的操作系统版本
     */
    @Field(type = FieldType.Keyword)
    private String osVersion;
    /**
     * UA
     */
    @Field(type = FieldType.Keyword)
    private String userAgent;

    // ---------------------- 归因 -----------------------
    /**
     * 获得归因的媒体渠道或restricted（受限渠道）
     */
    @Field(type = FieldType.Keyword)
    private String mediaSource;
    /**
     * 媒体渠道的流量入口，如Google的流量入口Youtube，Facebook的流量入口Instagram
     */
    @Field(type = FieldType.Keyword)
    private String afChannel;
    /**
     * 代理商或PMD
     */
    @Field(type = FieldType.Keyword)
    private String afPrt;
    /**
     * 广告系列名称
     */
    @Field(type = FieldType.Keyword)
    private String campaign;
    /**
     * 广告系列ID
     */
    @Field(type = FieldType.Keyword)
    private String afCId;
    /**
     * 由一个或多个素材组成的广告组
     */
    @Field(type = FieldType.Keyword)
    private String afAdset;
    /**
     * 广告组ID
     */
    @Field(type = FieldType.Keyword)
    private String afAdsetId;
    /**
     * 如横幅、页脚
     */
    @Field(type = FieldType.Keyword)
    private String afAdType;

    // ---------------------- 应用 -----------------------
    /**
     * AppsFlyer后台的非重应用标识符
     */
    @Field(type = FieldType.Keyword)
    private String appId;
    /**
     * 由广告主设置 app名字
     */
    @Field(type = FieldType.Keyword)
    private String appName;
    /**
     * 由广告主设置
     */
    @Field(type = FieldType.Keyword)
    private String appVersion;
    /**
     * 由广告主设置的应用用户非重ID
     */
    @Field(type = FieldType.Keyword)
    private String customerUserId;

    /*** 事件 ***/
    /**
     * 由应用发送的归因事件类型或应用内事件名称。
     */
    @Field(type = FieldType.Keyword)
    private String eventName;
    /**
     * SDK或S2S
     */
    @Field(type = FieldType.Keyword)
    private String eventSource;
    /**
     * 事件发生时间
     */
    @Field(type = FieldType.Date)
    private Date eventTime;
    /**
     * 从SDK发出的详细事件内容。
     */
    @Field(type = FieldType.Keyword)
    private String eventValue;


    // ---------------------- 设备位置 -----------------------
    /**
     * 根据SDK上报的设备IP地址判断
     */
    @Field(type = FieldType.Keyword)
    private String state;
    /**
     * 根据设备IP判断出的粒度最细的位置信息。该值通常会包含城市名称, 但也可能出现城市行政区划等更准确的位置
     */
    @Field(type = FieldType.Keyword)
    private String city;
    /**
     * 根据SDK上报的设备IP地址判断
     */
    @Field(type = FieldType.Keyword)
    private String postalCode;
    /**
     * IP地址：IPV4或IPV6。AppsFlyer会根据IP地址判断用户位置。如有需要，广告主可以选择在报告和回传中掩盖IP地址。
     */
    @Field(type = FieldType.Ip)
    private String ip;

    /**
     * 创建时间
     */
    @Field(type = FieldType.Date)
    private Date createTime;
}
