package com.epoch.loan.workshop.common.params.params.request;

import com.epoch.loan.workshop.common.params.params.BaseParams;
import lombok.Data;

/**
 * @author : ljy
 * @packageName : com.epoch.loan.workshop.common.params.params.request
 * @className : BindBankCardParams
 * @createTime : 22/3/29 15:45
 * @description : 绑卡接口入参封装
 */
@Data
public class BindBankCardParams extends BaseParams {

    /**
     * 订单id
     */
    private String orderNo;

    /**
     * 银行账户
     */
    private String bankCard;

    /**
     * 开户行ifscCode
     */
    private String openBank;

    /**
     * 用户姓名
     */
    private String userName;

    /**
     * 用户身份证号，新绑卡时有值，选择已有旧卡时可能为空
     */
    private String idNumber;

    /**
     * 用户手机号，新绑卡时有值，选择已有旧卡时为空
     */
    private String userMobile;

    /**
     * 开户行地址，新绑卡时有值，选择已有旧卡时为空
     */
    private String bankAddress;

    /**
     * 类型标识 1-多推
     */
    private Integer appType;
}
