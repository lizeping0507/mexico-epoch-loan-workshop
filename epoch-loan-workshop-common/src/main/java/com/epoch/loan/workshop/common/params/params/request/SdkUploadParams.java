package com.epoch.loan.workshop.common.params.params.request;

import com.epoch.loan.workshop.common.params.params.BaseParams;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

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
     * 订单id
     */
    private String orderNo;

    /**
     * 抓取状态 1：成功 2 成功失败
     */
    private Integer reportStatus;

    /**
     * sdk抓取epoch响应码
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

    /**
     * 验证 订单编号 是否合法
     *
     * @return true或false
     */
    public boolean isOrderNoLegal() {
        if (StringUtils.isEmpty(this.orderNo)) {
            return false;
        }
        return true;
    }

    /**
     * 验证 抓取状态 是否合法
     *
     * @return true或false
     */
    public boolean isReportStatusLegal() {
        if (ObjectUtils.isEmpty(this.reportStatus)) {
            return false;
        }
        return true;
    }

    /**
     * 验证 sdk抓取epoch响应码 是否合法
     *
     * @return true或false
     */
    public boolean isCodeLegal() {
        if (StringUtils.isEmpty(this.code)) {
            return false;
        }
        return true;
    }

    /**
     * 验证 epoch抓取响应信息 是否合法
     *
     * @return true或false
     */
    public boolean isMessageLegal() {
        if (StringUtils.isEmpty(this.message)) {
            return false;
        }
        return true;
    }

    /**
     * 验证 抓取类型 是否合法
     *
     * @return true或false
     */
    public boolean isTypeLegal() {
        if (StringUtils.isEmpty(this.type)) {
            return false;
        }
        return true;
    }
}
