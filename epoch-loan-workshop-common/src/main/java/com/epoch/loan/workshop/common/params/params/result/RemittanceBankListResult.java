package com.epoch.loan.workshop.common.params.params.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.params.params.result
 * @className : RemittanceBankListResult
 * @createTime : 2022/4/21 16:18
 * @description : TODO 一句话描述该类的功能
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RemittanceBankListResult implements Serializable {

    /**
     * 银行列表
     */
    private List<String> list;
}
