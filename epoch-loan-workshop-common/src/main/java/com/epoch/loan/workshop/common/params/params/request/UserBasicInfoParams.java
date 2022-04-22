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
public class UserBasicInfoParams extends BaseParams {
    /**
     * 月收入
     */
    private String monthlyIncome;

    /**
     * 发薪周期
     */
    private String payPeriod;

    /**
     * 职业
     */
    private String occupation;

    /**
     * 工资发放方式
     */
    private String payMethod;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 学历
     */
    private String education;

    /**
     * 婚姻状况
     */
    private String marital;

    /**
     * 借款目的
     */
    private String loanPurpose;

    /**
     * 父亲名字
     */
    private String customFatherName;

    /**
     * 全名
     */
    private String customFullName;

    /**
     * 母亲名字
     */
    private String customMotherName;

    /**
     * 姓名
     */
    private String customName;


    /**
     * 月收入 字段校验
     */
    public boolean isMonthlyIncomeLegal() {
        if (StringUtils.isBlank(this.monthlyIncome)) {
            return false;
        }
        return true;
    }

    /**
     * 发薪周期 字段校验
     */
    public boolean isPayPeriodLegal() {
        if (StringUtils.isBlank(this.payPeriod)) {
            return false;
        }
        return true;
    }

    /**
     * 职业 字段校验
     */
    public boolean isOccupationLegal() {
        if (StringUtils.isBlank(this.occupation)) {
            return false;
        }
        return true;
    }

    /**
     * 工资发放方式 字段校验
     */
    public boolean isPayMethodLegal() {
        if (StringUtils.isBlank(this.payMethod)) {
            return false;
        }
        return true;
    }

    /**
     * 邮箱 字段校验
     */
    public boolean isEmailLegal() {
        if (StringUtils.isBlank(this.email)) {
            return false;
        }
        return true;
    }

    /**
     * 学历 字段校验
     */
    public boolean isEducationLegal() {
        if (StringUtils.isBlank(this.education)) {
            return false;
        }
        return true;
    }

    /**
     * 婚姻状况 字段校验
     */
    public boolean isMaritalLegal() {
        if (StringUtils.isBlank(this.marital)) {
            return false;
        }
        return true;
    }

    /**
     * 借款目的 字段校验
     */
    public boolean isLoanPurposeLegal() {
        if (StringUtils.isBlank(this.loanPurpose)) {
            return false;
        }
        return true;
    }

    /**
     * 父亲名字 字段校验
     */
    public boolean isCustomFatherNameLegal() {
        if (StringUtils.isBlank(this.customFatherName)) {
            return false;
        }
        return true;
    }

    /**
     * 全名 字段校验
     */
    public boolean isCustomFullNameLegal() {
        if (StringUtils.isBlank(this.customFullName)) {
            return false;
        }
        return true;
    }

    /**
     * 母亲名字 字段校验
     */
    public boolean isCustomMotherNameLegal() {
        if (StringUtils.isBlank(this.customMotherName)) {
            return false;
        }
        return true;
    }

    /**
     * 姓名 字段校验
     */
    public boolean isCustomNameLegal() {
        if (StringUtils.isBlank(this.customName)) {
            return false;
        }
        return true;
    }
}
