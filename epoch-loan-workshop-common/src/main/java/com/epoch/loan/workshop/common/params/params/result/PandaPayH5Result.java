package com.epoch.loan.workshop.common.params.params.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
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
    /**
     * 时间后半部分
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "America/Mexico_City")
    private Date shortTime;

}
