package com.epoch.loan.workshop.common.entity.mysql;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.entity.mysql
 * @className : PlatformOrderPushRepaymentEntity
 * @createTime : 2021/11/24 11:07
 * @description : 还款计划推送接口表 ?
 */
@Data
public class PlatformOrderPushRepaymentEntity {
	/**
	 * 主键
	 */
	private Long id;
	
	/**
	 * 订单编号	string	否	查询账单的订单编号
	 */
	private String orderNo;

	/**
	 * 银行名称	string	否	还款银行名，中文名，不要传代码，会展示给用户
	 */
	private String openBank;

	/**
	 * 银行卡号	string	否	还款银行卡号
	 */
	private String bankCard;

	/**
	 * 是否支持提前全部结清	int	是	仅多期产品需回传，1=支持；0=不支持。
	 */
	private String canPrepay;

	/**
	 * 可提前全部结清的开始时间	timestamp	是	当支持提前全部结清时需回传。十位时间戳
	 */
	private Long canPrepayTime;

	/**
	 * 建立时间
	 */
	private Date createTime;

}
