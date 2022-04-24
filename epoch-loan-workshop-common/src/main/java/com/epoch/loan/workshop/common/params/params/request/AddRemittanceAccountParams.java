package com.epoch.loan.workshop.common.params.params.request;

import com.epoch.loan.workshop.common.params.params.BaseParams;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.params.params.request
 * @className : AddRemittanceAccountParams
 * @createTime : 2022/4/21 15:33
 * @description : 新增放款账户
 */
@Data
public class AddRemittanceAccountParams extends BaseParams {

    /**
     * 账户账号
     */
    private String accountNumber;

    /**
     * 银行
     */
    private String bank;

    /**
     * 姓名
     */
    private String name;

    /**
     * 类型 0:借记卡 1:clabe账户
     */
    private Integer type;

    /**
     * 验证 类型 是否合法
     *
     * @return true或false
     */
    public boolean isTypeLegal() {
        if (this.type == null) {
            return false;
        }
        return true;
    }

    /**
     * 验证 银行 是否合法
     *
     * @return true或false
     */
    public boolean isNameLegal() {
        if (StringUtils.isEmpty(this.name)) {
            return false;
        }
        return true;
    }

    /**
     * 验证 银行 是否合法
     *
     * @return true或false
     */
    public boolean isBankLegal() {
        if (StringUtils.isEmpty(this.bank)) {
            return false;
        }
        return true;
    }

    /**
     * 验证 账户账号 是否合法
     *
     * @return true或false
     */
    public boolean isAccountNumberLegal() {
        if (StringUtils.isEmpty(this.accountNumber)) {
            return false;
        }
        return true;
    }
}
