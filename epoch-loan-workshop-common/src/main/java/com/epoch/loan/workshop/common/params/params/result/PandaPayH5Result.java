package com.epoch.loan.workshop.common.params.params.result;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author Shangkunfeng
 * @packagename : com.epoch.loan.workshop.common.params.result
 * @className : PandaPayH5Result
 * @createTime : 2022/03/29 15:32
 * @Description: PandaPay H5数据回参
 */
@Data
public class PandaPayH5Result implements Serializable {

    private String clabe;
    /**
     * code拆分
     */
    private List<String> spiltCode;
    /**
     * 金额
     */
    private String amount;
    /**
     * 条形码
     */
    private String barCode;
    /**
     * 手续费
     */
    private String fee;
    /**
     * 本金
     */
    private String actualAmount;

}
