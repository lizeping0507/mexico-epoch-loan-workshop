package com.epoch.loan.workshop.common.constant;

/**
 * @author 魏玉强
 * @packagename : com.epoch.loan.workshop.common.constant
 * @className : OcrField
 * @createTime : 2022/04/19 15:24
 * @Description: advance 相关常量
 */
public class OcrField {

    /**
     * advance认证通过code
     */
    public static final String ADVANCE_SUCCESS_CODE = "SUCCESS";

    /**
     * advance认证通过message
     */
    public static final String ADVANCE_SUCCESS_MESSAGE = "OK";

    /**
     * advance accessKey key
     */
    public static final String ADVANCE_ACCESS_KEY_KEY = "X-ADVAI-KEY";

    /**
     * advance appId key
     */
    public static final String ADVANCE_APP_ID_KEY = "applicationId";

    /**
     * advance license有效时间
     */
    public static final String ADVANCE_LICENSE_EFFECTIVE_SECONDS = "licenseEffectiveSeconds";

    /**
     * advance license请求设置的有效时间
     */
    public static final String ADVANCE_LICENSE_SECONDS = "86400";

    /**
     * advance用户面部照片的标识符
     */
    public static final String ADVANCE_FACE_IMAGE_ID = "livenessId";

    /**
     * advance图像格式，IMAGE_URL 或 IMAGE_BASE64。默认值为 IMAGE_URL
     */
    public static final String ADVANCE_RESULT_TYPE = "resultType";

    /**
     * advance图像默认格式
     */
    public static final String ADVANCE_DEFAULT_IMAGE_TYPE = "IMAGE_URL";
}
