package com.epoch.loan.workshop.common.params.params.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author 魏玉强
 * @packagename : com.epoch.loan.workshop.common.params.result
 * @className : ChannelTypeResult
 * @createTime : 2022/03/28 15:39
 * @Description: 聚道结果封装
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChannelTypeResult implements Serializable {

    /**
     * 聚道类型：OCR时  2-ACC  3-闪云金科 4-advance
     */
    private String type;
}
