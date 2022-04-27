package com.epoch.loan.workshop.common.params.params.request;

import com.epoch.loan.workshop.common.params.params.BaseParams;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.params.params.request;
 * @className : SaveFileParams
 * @createTime : 2022/3/31 14:34
 * @description : 用户OCR证件信息上传参数封装
 */
@Data
public class SaveFileParams extends BaseParams {

    /**
     * 活体照片
     */
    private byte[] faceImgData;

    /**
     * 活体照片类型
     */
    private String faceImgType;

    /**
     * KFC卡正面
     */
    private byte[] idFrontImgData;

    /**
     * KFC卡正面照片类型
     */
    private String idFrontImgType;

    /**
     * KFC卡反面
     */
    private byte[] idBackImgData;

    /**
     * KFC卡反面照片类型
     */
    private String idBackImgType;

    /**
     * KFC卡正面 识别出来的信息
     */
    private String frontJson;

    /**
     * KFC卡背面 识别出来的信息
     */
    private String backJson;

    /**
     * 手填姓名
     */
    private String name;

    /**
     * 手填父亲姓氏
     */
    private String fatherName;

    /**
     * 手填母亲姓氏
     */
    private String motherName;

    /**
     * 手填证件号
     */
    private String curp;

    /**
     * rfc
     */
    private String rfc;

    /**
     * 验证  KFC卡正面 识别出来的信息 是否合法
     *
     * @return true或false
     */
    public boolean isFrontJsonLegal() {
        if (StringUtils.isEmpty(this.frontJson)) {
            return false;
        }
        return true;
    }

    /**
     * 验证  KFC卡背面 识别出来的信息 是否合法
     *
     * @return true或false
     */
    public boolean isBackJsonLegal() {
        if (StringUtils.isEmpty(this.backJson)) {
            return false;
        }
        return true;
    }

    /**
     * 验证  手填姓名 是否合法
     *
     * @return true或false
     */
    public boolean isNameLegal() {
        if (StringUtils.isEmpty(this.name)) {
            return false;
        }
        return true;
    }

    /**
     * 验证  手填父亲姓氏 是否合法
     *
     * @return true或false
     */
    public boolean isFatherNameLegal() {
        if (StringUtils.isEmpty(this.fatherName)) {
            return false;
        }
        return true;
    }

    /**
     * 验证  手填母亲姓氏 是否合法
     *
     * @return true或false
     */
    public boolean isMotherNameLegal() {
        if (StringUtils.isEmpty(this.motherName)) {
            return false;
        }
        return true;
    }

    /**
     * 验证  手填证件号 是否合法
     *
     * @return true或false
     */
    public boolean isCurpLegal() {
        if (StringUtils.isEmpty(this.curp)) {
            return false;
        }
        return true;
    }
}
