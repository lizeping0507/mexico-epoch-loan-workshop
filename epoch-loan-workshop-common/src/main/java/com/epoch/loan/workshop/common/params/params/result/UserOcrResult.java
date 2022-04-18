package com.epoch.loan.workshop.common.params.params.result;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 魏玉强
 * @packagename : com.epoch.loan.workshop.common.params.result
 * @className : UserOcrResult
 * @createTime : 2022/04/01 19:56
 * @Description: advance识别出来的ocr信息
 */
@Data
public class UserOcrResult implements Serializable {

    /**
     * 识别出来的证件信息json
     */
    private String info;

    /**
     * 识别类型1 aadhar正面 2 aadhar背面 3 pan卡正面
     */
    private Integer type;

}
