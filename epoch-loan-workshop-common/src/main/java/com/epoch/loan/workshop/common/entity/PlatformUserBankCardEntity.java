package com.epoch.loan.workshop.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.entity
 * @className : PlatformUserBankCardEntity
 * @createTime : 2021/11/22 10:54
 * @description : 用户银行卡实体类 TODO 老表
 */
@Data
public class PlatformUserBankCardEntity {
    /**
     * 主键
     */
    private Long id;

    /**
     * 用户表主键
     */
    private Long userId;

    /**
     * 绑卡卡号	string	否	绑卡卡号
     */
    private String bankCard;

    /**
     * 绑卡开户行	string	否	绑定银行卡的开户行（例如：ICBC，ABC，CMB，CCB等）,具体见底部附录
     */
    private String openBank;

    /**
     * 用户姓名，新绑卡时有值，选择已有卡时可能为空
     */
    private String userName;

    /**
     * 用户身份证号，新绑卡时有值，选择已有旧卡时可能为空
     */
    private String idNumber;

    /**
     * 手机号	string	是	用户手机号，新绑卡时有值，选择已有旧卡时为空
     */
    private String userMobile;

    /**
     * 开户行地址	string	是	开户行地址，新绑卡时有值，选择已有旧卡时为空
     */
    private String bankAddress;

    /**
     * 绑卡的卡类型	int	是	新绑卡时有值，选择已有旧卡时为空。 0：储蓄卡，1：信用卡
     */
    private Integer bankCardType;

    /**
     * 是否曾经绑定。 0：未曾绑定，1：已经绑定
     */
    private Integer binded;

    /**
     * 订单编号
     */
    private String orderNo;
}
