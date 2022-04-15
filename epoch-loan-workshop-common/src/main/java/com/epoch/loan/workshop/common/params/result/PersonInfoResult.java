package com.epoch.loan.workshop.common.params.result;

import com.epoch.loan.workshop.common.params.result.model.Conditions;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.params.result;
 * @className : PersonInfoResult
 * @createTime : 2022/3/29 16:04
 * @description : 个人信息结果封装
 */
@Data
public class PersonInfoResult implements Serializable {

    /**
     * 用户id
     */
    private Long userId;
    /**
     * 收入方式
     */
    private Conditions incomeWay;
    /**
     * 与家人的关系
     */
    private Conditions familyRelationship;
    /**
     * 家人姓名
     */
    private String familyName;
    /**
     * 家人手机
     */
    private String familyPhone;
    /**
     * 朋友姓名
     */
    private String friendName;
    /**
     * 朋友手机
     */
    private String friendPhone;
    /**
     * 住房类型
     */
    private Conditions houseType;
    /**
     * 孩子数量
     */
    private Conditions childNum;
    /**
     * ?
     */
    private List<Conditions> designationList;
    /**
     * 收入方式集合
     */
    private List<Conditions> incomeWayList;
    /**
     * 关系集合
     */
    private List<Conditions> familyRelationshipList;
    /**
     * 居住类型集合
     */
    private List<Conditions> houseTypeList;
    /**
     * 孩子数量集合
     */
    private List<Conditions> childNumList;
    /**
     * 是否需要sdk抓取并上报数据
     */
    private Boolean needCatchData;
    /**
     * 订单号?
     */
    private String orderNo;
}
