package com.epoch.loan.workshop.common.params.params.request;

import com.epoch.loan.workshop.common.params.params.BaseParams;
import lombok.Data;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.params.params.request;
 * @className : PersonInfoParams
 * @createTime : 2022/3/29 16:07
 * @description : 用户个人信息请求参数
 */
@Data
public class PersonInfoParams extends BaseParams {
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 产品id
     */
    private Long productId;
    /**
     * 居住类型
     */
    private Integer houseType;
    /**
     * 孩子数量
     */
    private Integer childNum;
    /**
     *
     */
    private Integer designation;
    /**
     * 收入方式
     */
    private Integer incomeWay;
    /**
     * 与家人的关系
     */
    private Integer familyRelationship;
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
}
