package com.epoch.loan.workshop.common.params.params.result;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author 魏玉强
 * @packagename : com.epoch.loan.workshop.common.params.result
 * @className : PandaPayH5Result
 * @createTime : 2022/03/29 15:32
 * @Description: PandaPay H5数据回参
 */
@Data
public class PandaPayH5Result implements Serializable {

    private String code;
    private List<String> spiltCode;
    private String amount;

}
