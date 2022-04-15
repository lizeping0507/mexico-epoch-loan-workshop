package com.epoch.loan.workshop.common.params.result;

import com.epoch.loan.workshop.common.params.result.model.DictBankCardListVo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author 魏玉强
 * @packagename : com.epoch.loan.workshop.common.params.result
 * @className : AddBankcardResult
 * @createTime : 2022/04/01 20:54
 * @Description: 新增银行卡
 */
@Data
public class AddBankcardResult implements Serializable {

    /**
     * 订单编号
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
     * 开户行名称
     */
    private String openBankName;

    /**
     * 用户姓名
     */
    private String userName;

    /**
     * 用户身份证号
     */
    private String idNumber;

    /**
     * 用户手机号
     */
    private String userMobile;

    /**
     * 开户行地址
     */
    private Long bankAddress;

    /**
     * 开户行
     */
    private String bankAddressName;

    /**
     * 银行卡字典
     */
    private List<DictBankCardListVo> bankList;
}
