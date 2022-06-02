package com.epoch.loan.workshop.common.params.params.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author 魏玉强
 * @packagename : com.epoch.loan.workshop.common.params.params.request
 * @className : CollectionReductionParams
 * @createTime : 2022/05/31 11:55
 * @Description: 催收减免金额请求参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CollectionReductionParams implements Serializable {

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 产品名称
     */
    private String orgId;

    /**
     * 减免金额
     */
    private BigDecimal subOverdueAmount;

    /**
     * 验证 订单编号 是否合法
     *
     * @return true或false
     */
    public boolean isOrderNoLegal() {
        if (StringUtils.isEmpty(this.orderNo)) {
            return false;
        }
        return true;
    }

    /**
     * 验证 订单编号 是否合法
     *
     * @return true或false
     */
    public boolean isOrgIdLegal() {
        if (StringUtils.isEmpty(this.orgId)) {
            return false;
        }
        return true;
    }

    /**
     * 验证  产品名称 是否合法
     *
     * @return true或false
     */
    public boolean isSubOverdueAmountLegal() {
        if (ObjectUtils.isEmpty(this.subOverdueAmount)) {
            return false;
        }
        return true;
    }
}
