package com.epoch.loan.workshop.common.entity.mysql;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

/**
 * @author : ljy
 * @packageName : com.epoch.loan.workshop.common.entity.mysql
 * @className : LoanUserBankCardEntity
 * @createTime : 2022/4/18 14:24
 * @description : 用户绑定放款卡信息
 */
@Data
public class LoanUserBankCardEntity {
    /**
     * 用户表主键
     */
    @TableId(type= IdType.INPUT)
    private Long userId;
    /**
     * 绑卡表主键
     */
    private Long cardId;
    /**
     * 绑卡卡号	string	否	绑卡卡号
     */
    private String bankCard;
    /**
     * 绑卡开户行	string	否	绑定银行卡的开户行（例如：ICBC，ABC，CMB，CCB等）,具体见底部附录
     */
    private String openBank;

    /**
     * 建立时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
