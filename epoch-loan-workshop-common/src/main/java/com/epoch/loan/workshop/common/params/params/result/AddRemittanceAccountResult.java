package com.epoch.loan.workshop.common.params.params.result;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.params.params.result
 * @className : AddRemittanceAccountResult
 * @createTime : 2022/4/22 15:18
 * @description : 新增放款账户
 */
@Data
@NoArgsConstructor
public class AddRemittanceAccountResult implements Serializable {
    /**
     * 新增id
     */
    private String id;
}
