package com.epoch.loan.workshop.common.params.params.request;

import com.epoch.loan.workshop.common.params.params.BaseParams;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.params.params.request;
 * @className : UserBasicInfoParams
 * @createTime : 2022/3/29 16:08
 * @description : 用户个人信息请求参数
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
     * 紧急联系人信息
     */
    private String contacts;


    /**
     * 字段校验-月收入
     */
    public boolean isMonthlyIncomeLegal() {
        if (StringUtils.isEmpty(this.monthlyIncome)) {
            return false;
        }
        return true;
    }

    /**
     * 字段校验-发薪周期
     */
    public boolean isPayPeriodLegal(){
        if (StringUtils.isEmpty(this.payPeriod)){
            return false;
        }
        return true;
    }

    /**
     * 字段校验-职业
     */
    public boolean isOccupationLegal(){
        if (StringUtils.isEmpty(this.occupation)){
            return false;
        }
        return true;
    }

    /**
     * 字段校验-工资发放方式
     */
    public boolean isPayMethodLegal(){
        if (StringUtils.isEmpty(this.payMethod)){
            return false;
        }
        return true;
    }


    /**
     * 字段校验-紧急联系人信息
     */
    public boolean isContactsLegal(){
        if (StringUtils.isEmpty(this.contacts)){
            return false;
        }
        return true;
    }
}
