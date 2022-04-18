package com.epoch.loan.workshop.common.params.params.result;

import com.epoch.loan.workshop.common.params.params.result.model.BankCardList;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author : ljy
 * @packageName : com.epoch.loan.workshop.common.params.result
 * @className : BankCardResult
 * @createTime : 22/3/29 10:57
 * @description : 银行卡列表接口回参封装
 */
@Data
public class BankCardResult implements Serializable {

    /**
     * 银行卡列表
     */
    private List<BankCardList> list;
}
