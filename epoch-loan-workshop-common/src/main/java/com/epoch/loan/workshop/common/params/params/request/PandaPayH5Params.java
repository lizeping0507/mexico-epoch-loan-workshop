package com.epoch.loan.workshop.common.params.params.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author : lyf
 * @packageName : com.epoch.loan.workshop.common.params.params.request;
 * @className : PandaPayH5Params
 * @createTime : 2022/3/01 16:10
 * @description : pandaPay H5数据请求参数
 */
@Data
public class PandaPayH5Params implements Serializable {

    private static final long serialVersionUID = 116541653165465L;

    /**
     * 支付记录id
     */
    private String id;

}
