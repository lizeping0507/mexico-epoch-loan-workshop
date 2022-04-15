package com.epoch.loan.workshop.common.dao.mysql;

import com.epoch.loan.workshop.common.entity.mysql.PlatformUserIdImgEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.dao.mysql
 * @className : PlatformUserIdImgDao
 * @createTime : 2021/11/25 10:43
 * @description : 用户照片
 */
@Mapper
public interface PlatformUserIdImgDao {

    /**
     * 查询用户照片
     *
     * @param userId
     * @return
     */
    PlatformUserIdImgEntity findUserIdImg(String userId);
}
