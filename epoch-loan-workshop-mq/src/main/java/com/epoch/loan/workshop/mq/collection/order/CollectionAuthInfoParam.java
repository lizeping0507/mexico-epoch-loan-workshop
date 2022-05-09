package com.epoch.loan.workshop.mq.collection.order;

import lombok.Data;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA 2020.1
 *
 * @author 魏玉强
 * @version 1.0
 * @date 2022-03-08 15:57
 * @Description:
 * @program: epoch-loan-workshop
 * @packagename: com.epoch.loan.workshop.mq.collection.order
 */
@Data
public class CollectionAuthInfoParam implements Serializable {

    /**
     * 银行卡认证结果 0-失败，1-成功
     */
    private Integer bankAuthResult;

    /**
     * 联系人认证结果 0-失败，1-成功
     */
    private Integer contactAuthResult;

    /**
     * 个人信息认证结果 0-失败，1-成功
     */
    private Integer personalAuthResult;

    /**
     * 身份证明验证结果 0-失败，1-成功
     */
    private Integer idAuthResult;

}
