package com.epoch.loan.workshop.common.dao.mysql;

import com.epoch.loan.workshop.common.entity.mysql.PlatformUserBankCardEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.dao.mysql
 * @className : PlatformBankCardDao
 * @createTime : 2021/11/22 10:51
 * @description : 用户银行卡
 */
@Mapper
public interface PlatformUserBankCardDao {
    /**
     * 查询用户银行卡信息
     *
     * @param userId 用户Id
     */
    PlatformUserBankCardEntity findUserBankCard(String userId, String orderNo);

    /**
     * 查询用户银行卡信息
     *
     * @param id 银行卡Id
     */
    PlatformUserBankCardEntity findUserBankCardById(String id);

}
