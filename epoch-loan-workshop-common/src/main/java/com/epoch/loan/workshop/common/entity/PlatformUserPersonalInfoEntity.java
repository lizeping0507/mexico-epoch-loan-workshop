package com.epoch.loan.workshop.common.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.entity
 * @className : PlatformUserPersonalInfoEntity
 * @createTime : 2021/11/25 11:07
 * @description : 用户个人信息实体类
 */
@Data
public class PlatformUserPersonalInfoEntity {
    /**
     * id,与user表关联
     */
    private Long id;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 现所在地的邮政编码
     */
    private String currentPinCode;

    /**
     * 现所在城市
     */
    private String currentCity;

    /**
     * 现所在地详细地址
     */
    private String currentAddress;

    /**
     * 公司名称
     */
    private String companyName;

    /**
     * 紧急联系人A关系	string	1:父母；2:配偶；3:兄弟；4:姐妹；
     */
    private Integer emergencyContactPersonARelationship;

    /**
     * 紧急联系人B关系	string	1:同事；2:配偶；3:兄弟；4:姐妹；
     */
    private Integer emergencyContactPersonBRelationship;

    /**
     * 紧急联系人A姓名	string
     */
    private String emergencyContactPersonAName;

    /**
     * 紧急联系人A电话	string
     */
    private String emergencyContactPersonAPhone;

    /**
     * 紧急联系人B姓名	string
     */
    private String emergencyContactPersonBName;

    /**
     * 紧急联系人B电话	string
     */
    private String emergencyContactPersonBPhone;

    /**
     * 职位
     */
    private Integer designation;

    /**
     * 收入方式
     */
    private Integer incomeWay;

    /**
     * 孩子数量
     */
    private Integer childrenNum;

    /**
     * 住宿类型
     */
    private Integer accommodationType;
}
