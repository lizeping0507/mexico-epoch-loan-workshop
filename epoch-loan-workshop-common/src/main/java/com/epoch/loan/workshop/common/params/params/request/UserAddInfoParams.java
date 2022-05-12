package com.epoch.loan.workshop.common.params.params.request;

import com.epoch.loan.workshop.common.params.params.BaseParams;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.params.params.request;
 * @className : UserBasicInfoParams
 * @createTime : 2022/3/29 16:08
 * @description : 用户基本信息请求参数
 */
@Data
public class UserAddInfoParams extends BaseParams {
    /**
     * 用户填写生日
     */
    private String customDateOfBirth;

    /**
     * 用户填写性别
     */
    private String customGenter;

    /**
     * 孩子数量
     */
    private String childrenNumber ;

    /**
     * 居住类型
     */
    private String liveType ;

    /**
     * 学历
     */
    private String education ;

    /**
     * 婚姻状况
     */
    private String marital ;

    /**
     * 借款目的
     */
    private String loanPurpose ;

    /**
     * 邮箱
     */
    private String email ;


    /**
     * 邮箱
     */
    public boolean isEmailLegal(){
        if (StringUtils.isEmpty(this.email)){
            return false;
        }
        return true;
    }

    /**
     * 字段校验-用户填写生日
     */
    public boolean isCustomDateOfBirthLegal(){
        if (StringUtils.isEmpty(this.customDateOfBirth)){
            return false;
        }
        return true;
    }

    /**
     * 字段校验-用户填写性别
     */
    public boolean isCustomGenterLegal(){
        if (StringUtils.isEmpty(this.customGenter)){
            return false;
        }
        return true;
    }

    /**
     * 字段校验-孩子数量
     */
    public boolean isChildrenNumberLegal(){
        if (StringUtils.isEmpty(this.childrenNumber)){
            return false;
        }
        return true;
    }

    /**
     * 字段校验-居住类型
     */
    public boolean isLiveTypeLegal(){
        if (StringUtils.isEmpty(this.liveType)){
            return false;
        }
        return true;
    }

    /**
     * 字段校验-学历
     */
    public boolean isEducationLegal(){
        if (StringUtils.isEmpty(this.education)){
            return false;
        }
        return true;
    }

    /**
     * 字段校验-婚姻状况
     */
    public boolean isMaritalLegal(){
        if (StringUtils.isEmpty(this.marital)){
            return false;
        }
        return true;
    }

    /**
     * 字段校验-借款目的
     */
    public boolean isLoanPurposeLegal(){
        if (StringUtils.isEmpty(this.loanPurpose)){
            return false;
        }
        return true;
    }
}
