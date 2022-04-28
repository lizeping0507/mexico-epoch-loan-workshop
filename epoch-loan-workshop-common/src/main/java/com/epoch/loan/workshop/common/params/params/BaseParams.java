package com.epoch.loan.workshop.common.params.params;

import com.epoch.loan.workshop.common.params.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.params.params
 * @className : BaseParams
 * @createTime : 2021/3/10 21:59
 * @description : 请求参数基类
 */
@Data
@NoArgsConstructor
public class BaseParams implements Serializable {
    private static final long serialVersionUID = 116541653165465L;

    /**
     * 请求流水号
     */
    public String serialNo;

    /**
     * Token
     */
    public String token;

    /**
     * app 名称
     */
    public String appName;

    /**
     * App版本
     */
    public String appVersion;

    /**
     * 设备类型
     */
    public String mobileType;

    /**
     * 用户信息
     */
    public User user;

    /**
     * 申请时的经纬度
     */
    private String gps;

    /**
     * 申请时的地址
     */
    private String gpsAddress;

    /**
     * Af推广id
     */
    private String gaid;


    /**
     * 验证 app名称 是否合法
     *
     * @return true或false
     */
    public boolean isAppNameLegal() {
        if (StringUtils.isEmpty(this.appName)) {
            return false;
        }
        return true;
    }


}
