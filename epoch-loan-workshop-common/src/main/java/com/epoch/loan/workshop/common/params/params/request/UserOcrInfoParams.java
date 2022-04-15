package com.epoch.loan.workshop.common.params.params.request;

import com.epoch.loan.workshop.common.params.params.BaseParams;
import lombok.Data;

/**
 * @author 魏玉强
 * @packagename : com.epoch.loan.workshop.common.params.params.request
 * @className : UserOcrInfoSaveParams
 * @createTime : 2022/03/28 18:13
 * @Description: 用户OCR识别信息保存请求参数封装类
 */
@Data
public class UserOcrInfoParams extends BaseParams {

    /**
     * 用户标识
     */
    private String userId;

    /**
     * 识别类型1 aadhar正面 2 aadhar背面 3 pan卡正面
     */
    private Integer type;

    /**
     * 识别出来的json串
     */
    private String info;
}
