package com.epoch.loan.workshop.common.params.params.result;

import lombok.Data;

import java.io.Serializable;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.params.result;
 * @className : PersonInfoUpdateResult
 * @createTime : 2022/3/29 16:04
 * @description : 个人信息更新结果封装
 */
@Data
public class PersonInfoUpdateResult implements Serializable {
    /**
     * 是否修改成功 0：失败，1：成功
     */
    private Integer status;
}
