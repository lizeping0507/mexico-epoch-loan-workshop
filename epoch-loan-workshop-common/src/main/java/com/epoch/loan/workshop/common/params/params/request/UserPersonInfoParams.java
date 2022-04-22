package com.epoch.loan.workshop.common.params.params.request;

import com.epoch.loan.workshop.common.params.params.BaseParams;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.params.params.request;
 * @className : UserBasicInfoParams
 * @createTime : 2022/3/29 16:08
 * @description : 用户个人信息请求参数
 */
@Data
public class UserPersonInfoParams extends BaseParams {
    /**
     * 紧急联系人信息(JSON)
     */
    private String contacts;
    /**
     * 孩子数量
     */
    private String childrenNumber;
    /**
     * 居住类型
     */
    private String liveType;


    /**
     * 紧急联系人信息 字段校验
     */
    public boolean isContactsLegal() {
        if (StringUtils.isBlank(this.contacts)) {
            return false;
        }
        return true;
    }
    /**
     * 孩子数量 字段校验
     */
    public boolean isChildrenNumberLegal() {
        if (StringUtils.isBlank(this.childrenNumber)) {
            return false;
        }
        return true;
    }
    /**
     * 居住类型 字段校验
     */
    public boolean isLiveTypeLegal() {
        if (StringUtils.isBlank(this.liveType)) {
            return false;
        }
        return true;
    }

}
