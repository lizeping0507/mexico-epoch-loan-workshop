package com.epoch.loan.workshop.common.entity.mysql;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

/**
 * @author : ljy
 * @packageName : com.epoch.loan.workshop.common.entity.mysql
 * @className : LoanUserModelEntity
 * @createTime : 2022/07/14 16:28
 * @description : 用户模式实体类
 */
@Data
public class LoanUserModelEntity {
    /**
     * userid
     */
    @TableId(type= IdType.INPUT)
    private Long userId;

    /**
     * appName
     */
    private String appName;

    /**
     * 用户模式 0-现金贷 1-贷超
     */
    private Integer modleTag;

    /**
     * 创建时间
     */
    private Date createTime;
}
