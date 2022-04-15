package com.epoch.loan.workshop.common.params.params.request;

import com.epoch.loan.workshop.common.params.params.BaseParams;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : ljy
 * @packageName : com.epoch.loan.workshop.common.params.params.request
 * @className : SdkUploadParams
 * @createTime : 22/3/30 15:21
 * @description : SDK上传同步回调接口入参封装
 */
@Data
@NoArgsConstructor
public class SdkUploadParams extends BaseParams {

    /**
     * 用户id
     */
    private String userId;

    /**
     * 订单id
     */
    private String orderNo;

    /**
     * 报告抓取状态1：成功 2 成功失败
     */
    private Integer reportStatus;

    /**
     * sdk抓取epoch相应吗
     */
    private String code;

    /**
     * epoch抓取响应信息
     */
    private String message;

    /**
     * 抓取类型：msg：短信 app: app img：相册 contact：通讯录 device：设备信息
     */
    private String type;
}
