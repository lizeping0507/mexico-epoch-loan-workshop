package com.epoch.loan.workshop.common.params.result;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author 魏玉强
 * @packagename : com.epoch.loan.workshop.common.params.result
 * @className : EditPasswordResult
 * @createTime : 2022/03/23 17:41
 * @Description:
 */
@Data
@NoArgsConstructor
public class EditPasswordResult implements Serializable {
    /**
     * 用户id
     */
    private String userId;

    /**
     * 用户手机号
     */
    private String phoneNumber;

    /**
     * 用户手机号掩码
     */
    private String savePhoneNumber;
}
