package com.epoch.loan.workshop.common.params.params.request;

import com.epoch.loan.workshop.common.params.params.BaseParams;
import lombok.Data;

/**
 * @author 魏玉强
 * @packagename : com.epoch.loan.workshop.common.params.params.request
 * @className : BankBranchParams
 * @createTime : 2022/03/29 15:29
 * @Description: 根据父级获取子集
 */
@Data
public class ParentParams extends BaseParams {

    /**
     * 父级id
     */
    private String parentId;
}
