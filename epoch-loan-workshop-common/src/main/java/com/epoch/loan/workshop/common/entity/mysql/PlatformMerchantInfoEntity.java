package com.epoch.loan.workshop.common.entity.mysql;

import lombok.Data;

import java.util.Date;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.entity.mysql
 * @className : PlatformMerchantInfoEntity
 * @createTime : 2021/11/24 17:55
 * @description : 机构详情实体类
 */
@Data
public class PlatformMerchantInfoEntity {

    /**
     * 商户主键
     */
    private Long merchantId;

    /**
     * 商户名称
     */
    private String name;

    /**
     * 商户简称
     */
    private String shortName;

    /**
     * 联系人
     */
    private String contact;

    /**
     * 联系电话
     */
    private String telephoneNumber;

    /**
     * 是否需要提供风控 0 :不需要 ; 1 :需要
     */
    private Integer riskManagement;

    /**
     * 建立时间
     */
    private Date createTime;

    /**
     * 座机或者客服电话
     */
    private String landline;

    /**
     * 联络邮箱
     */
    private String email;

    /**
     * 工作时间
     */
    private String workTime;

    /**
     * 公司主键
     */
    private Long companyId;
}
