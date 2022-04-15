package com.epoch.loan.workshop.common.params.result;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author 魏玉强
 * @packagename : com.epoch.loan.workshop.common.params.result
 * @className : ListResult
 * @createTime : 2022/03/29 15:32
 * @Description: 返回data类型是list集合
 */
@Data
public class ListResult implements Serializable {

    /**
     * 属性集合
     */
    private List list;

}
