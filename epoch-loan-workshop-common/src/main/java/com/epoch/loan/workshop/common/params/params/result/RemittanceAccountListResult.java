package com.epoch.loan.workshop.common.params.params.result;

import com.epoch.loan.workshop.common.params.params.result.model.RemittanceAccountList;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.params.params
 * @className : RemittanceAccountListResult
 * @createTime : 2022/4/21 14:43
 * @description : 放款账户列表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RemittanceAccountListResult implements Serializable {
    /**
     * 账户列表
     */
    private List<RemittanceAccountList> list;
}
