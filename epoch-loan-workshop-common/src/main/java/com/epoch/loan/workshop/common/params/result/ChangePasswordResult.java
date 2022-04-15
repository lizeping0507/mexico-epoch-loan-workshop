package com.epoch.loan.workshop.common.params.result;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author 魏玉强
 * @packagename : com.epoch.loan.workshop.common.params.result
 * @className : ChangePasswordResult
 * @createTime : 2022/03/23 15:16
 * @Description: 忘记密码结果封装
 */
@Data
@NoArgsConstructor
public class ChangePasswordResult implements Serializable {
    /**
     * token值
     */
    private String token;
    /**
     * 用户id
     */
    private String userId;
}
