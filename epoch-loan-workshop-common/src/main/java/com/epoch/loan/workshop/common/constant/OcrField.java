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
     * advance 身份证的类型key： INE_OR_IFE_FRONT or INE_OR_IFE_BACK
     */
    public static final String ADVANCE_CARD_TYPE = "cardType";

    /**
     * advance curp ocr认证正面标识
     */
    public static final String ADVANCE_USER_OCR_ID_FRONT = "INE_OR_IFE_FRONT";

    /**
     * advance curp ocr认证背面标识
     */
    public static final String ADVANCE_USER_OCR_ID_BACK = "INE_OR_IFE_BACK";

    /**
     * advance 发送文件图像key
     */
    public static final String ADVANCE_card_IMAGE = "image";

    /**
     * advance 发送带图请求头CONTENT_TYPE的值
     */
    public static final String ADVANCE_MULTIPART_VALUE = "multipart/form-data; boundary=----------ThIs_Is_tHe_bouNdaRY_$";

    /**
     * advance后台配置的 app包路径
     */
    public static final String ADVANCE_APP_ID_KEY = "applicationId";

    /**
     * redis中存储的advance授权码key
     */
    public static final String ADVANCE_LICENSE_NAME = "license";

    /**
     * redis中存储的advance过期时间key
     */
    public static final String ADVANCE_LICENSE_EXPIRE_TIME = "expireTime";

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

    // ================ advance相关配置key ===================

    /**
     * advance 通用key
     */
    public static final String ADVANCE_ACCESS_KEY = "accessKey";

    /**
     * advance 包名配置key
     */
    public static final String ADVANCE_APP_PACKAGE_NAME = "appPackageName";

    /**
     * advance 获取license请求地址key
     */
    public static final String ADVANCE_LICENSE_URL = "licenseUrl";

    /**
     * advance 获取活体检测结果请求地址key
     */
    public static final String ADVANCE_LIVENESS_SCORE_URL = "livenessScoreUrl";

    /**
     * advance 活体检测分建议阀值key
     */
    public static final String LIVENESS_THRESHOLD = "livenessThreshold";


    /**
     * advance 获取身份证信息请求地址key
     */
    public static final String ADVANCE_CARD_INFO_URL = "cardInfoUrl";

    /**
     * advance 获取人脸相似度请求地址key
     */
    public static final String ADVANCE_FACE_COMPARISON_URL = "faceComparisonUrl";

    /**
     * advance 获取人脸相似度建议阈值key
     */
    public static final String ADVANCE_FACE_COMPARISON_THRESHOLD = "faceComparisonThreshold";

    /** advance认证失败 */
    public static final String ADVANCE_OCR_NO_RESULT = "OCR_NO_RESULT";

    /** advance认证失败 */
    public static final String ADVANCE_OCR_NO_RESULT_MESSAGE = "OCR_NO_RESULT";

    /** advance 认证类型不匹配 */
    public static final String ADVANCE_CARD_TYPE_NOT_MATCH = "CARD_TYPE_NOT_MATCH";

    /** advance 认证类型不匹配 */
    public static final String ADVANCE_CARD_TYPE_NOT_MATCH_MESSAGE = "El tipo de tarjeta no coincide";

}
