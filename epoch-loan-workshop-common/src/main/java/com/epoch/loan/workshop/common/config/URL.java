package com.epoch.loan.workshop.common.config;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.constant
 * @className : URL 常量类
 * @createTime : 2022/2/17 14:36
 * @description : TODO 一句话描述该类的功能
 */
public class URL {
    /**
     * 一级
     */
    public static final String API = "/api";

    /**
     * 支付回调
     */
    public static final String PAYMENT_CALL_BACK = API + "/paymentCallBack";

    /**
     * 支付回调
     */
    public static final String INTERNAL = API + "/internal";

    /**
     * 还款回调
     */
    public static final String REPAYMENT_CALL_BACK = API + "/repaymentCallBack";

    /**
     * 还款
     */
    public static final String REPAYMENT = API + "/repayment";

    /**
     * 订单
     */
    public static final String ORDER = API + "/order";

    /**
     * 用户
     */
    public static final String USER = API + "/user";

    /**
     * 产品
     */
    public static final String PRODUCT = API + "/product";

    /**
     * 放款
     */
    public static final String REMITTANCE = API + "/remittance";

    /**
     * 手机短信
     */
    public static final String SHORT_MESSAGE = API + "/shortMessage";

    /**
     * ocr认证信息
     */
    public static final String OCR = API + "/ocr";

    /**
     * 银行卡信息
     */
    public static final String BANK_CARD = API + "/bankCard";

    /**
     * SDK信息
     */
    public static final String SDK = API + "/sdk";

    /**
     * SDK信息
     */
    public static final String H5 = API + "/h5";

    /**
     * 随机
     */
    public static final String RANDOM = API + "/random";

    /**
     * 催收还提
     */
    public static final String COLLECTION = API + "/collection";

    /**
     * 催收还提
     */
    public static final String AF = API + "/af";

    /*二级*/
    /**
     * 隐私协议
     */
    public static final String PRIVACY = "/privacy";
    /**
     * utr引导
     */
    public static final String VIDEO_UTR = "/video/utr";
    /**
     * InPay
     */
    public static final String HELP = "/help";
    /**
     * utr接口
     */
    public static final String REPAY_UTR = "/repayUTR";

    /**
     * 申请
     */
    public static final String APPLY = "/apply";


    /**
     * 合同
     */
    public static final String BIND_REMITTANCE_ACCOUNT = "/bindRemittanceAccount";

    /**
     * 注册
     */
    public static final String REGISTER = "/register";

    /**
     * 密码登录
     */
    public static final String LOGIN = "/login";

    /**
     * 是否已注册
     */
    public static final String IS_REGISTER = "/isRegister";

    /**
     * 是否已注册
     */
    public static final String APP_MASK_MODEL = "/appMaskModel";

    /**
     * 放款账户列表
     */
    public static final String REMITTANCE_ACCOUNT_LIST = "/remittanceAccountList";

    /**
     * 放款账户列表
     */
    public static final String ADD_REMITTANCE_ACCOUNT = "/addRemittanceAccount";

    /**
     * 放款银行列表
     */
    public static final String REMITTANCE_BANK_LIST = "/remittanceBankList";

    /**
     * 产品列表
     */
    public static final String LIST = "/list";


    /**
     * 产品详情
     */
    public static final String PRODUCT_DETAIL = "/productDetail";

    /**
     * 忘记密码
     */
    public static final String FORGOT_PWD = "/forgotPwd";

    /**
     * 更新密码
     */
    public static final String MODIFY_PASSWORD = "/modifyPassword";

    /**
     * 我的个人中心
     */
    public static final String MINE = "/mine";

    /**
     * 获取OCR认证聚道
     */
    public static final String OCR_CHANNEL_TYPE = "/ocrChannelType";

    /**
     * 获取advance license
     */
    public static final String OCR_ADVANCE_LICENSE = "/advanceLicense";

    /**
     * 获取advance 活体识别分
     */
    public static final String OCR_ADVANCE_LIVENESS_SCORE = "/advanceLivenessScore";

    /**
     * 获取保存的用户OCR信息
     */
    public static final String GET_USER_OCR_INFO = "/getOcrInfo";

    /**
     * 产品 - 推荐列表
     */
    public static final String PRODUCT_RECOMMEND_LIST = "/recommendList";

    /**
     * 订单 - 全部订单列表
     */
    public static final String ORDER_LIST_ALL = "/listAll";

    /**
     * 订单 - 待完成订单列表
     */
    public static final String ORDER_UN_FINISHED_LIST = "/unfinishedOrderList";

    /**
     * 订单 - 待还款订单列表
     */
    public static final String ORDER_UN_REPAYMENT_LIST = "/unRepaymentOrderList";

    /**
     * 多推订单 - 申请确认页
     */
    public static final String CONFIRM_MERGE_PUSH_APPLY = "/confirmMergePushApply";

    /**
     * 订单 - 订单详情
     */
    public static final String ORDER_DETAIL = "/detail";

    /**
     * 订单 - 申请确认页
     */
    public static final String ORDER_APPLY_CONFIRMATION = "/applyConfirmation";

    /**
     * SDK - SDK上传同步回调
     */
    public static final String SDK_UPLOAD_CALLBACK = "/sdkUploadCallBack";

    /**
     * SDK - 是否推送基本信息
     */
    public static final String SDK_IS_PUSH_INFO = "/sdkIsPushInfo";

    /**
     * OCR 文件保存
     */
    public static final String SAVE_FILE = "/saveFile";

    /**
     * 获取证件信息和人脸相似度
     */
    public static final String FACE_COMPARISON = "/faceComparison";

    /**
     * 用户OCR识别（advance获取证件信息)
     */
    public static final String USER_OCR_INFO = "/userOcrInfo";

    /**
     * 发起
     */
    public static final String PREPAY = "/prepay";

    /**
     * 保存用户基本信息
     */
    public static final String SAVE_ADD_INFO = "/addInfo/save";

    /**
     * 保存用户个人信息
     */
    public static final String SAVE_BASIC_INFO = "/basicInfo/save";

    /**
     * 保存用户信息
     */
    public static final String GET_INFO = "/info";

    /**
     * 发送验证码
     */
    public static final String SEND_SMSCODE = "/sendSmsCode";

    /**
     * pandaPay
     */
    public static final String PANDA_PAY = "/pandaPay";

    /**
     * pandaPay 验证用户
     */
    public static final String PANDA_PAY_VERIFY_USER = "/pandaPay/verifyUser";

    /**
     * pandaPay 验证用户
     */
    public static final String PANDA_PAY_AUTHORIZE = "/pandaPay/authorize";
    /**
     * 版本检查
     */
    public static final String CHECK_VERSION = "/checkVersion";
    /**
     * 用户客群
     */
    public static final String USER_TYPE = "/user/type";
    /**
     * PANDAPAY_OXXO_H5
     */
    public static final String PANDAPAY_H5 = "/pandaPay/h5";

    /**
     * jsonString
     */
    public static final String JSONString = "/jsonString";

    /**
     * 减免
     */
    public static final String REDUCTION = "/reduction";
}
