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
     * InPay
     */
    public static final String INPAY = "/inpay";

    /**
     * YEAHPAY
     */
    public static final String YEAHPAY = "/yeahpay";

    /**
     * SUNFLOWERPAY
     */
    public static final String SUNFLOWERPAY = "/sunflowerpay";

    /**
     * OCEANPAY
     */
    public static final String OCEANPAY = "/oceanpay";

    /**
     * ACPAY
     */
    public static final String ACPAY = "/acpay";

    /**
     * INCASHPAY
     */
    public static final String INCASHPAY = "/incash";

    /**
     * INCASHXIDPAY
     */
    public static final String INCASHXJDPAY = "/incashxjd";

    /**
     * TRUSTPAY
     */
    public static final String TRUSTPAY = "/trust";

    /**
     * QEPAY
     */
    public static final String QEPAY = "/qe";
    /**
     * HRPAY
     */
    public static final String HRPAY = "/hr";
    /**
     * utr接口
     */
    public static final String REPAY_UTR = "/repayUTR";

    /**
     * GLOBPAY
     */
    public static final String GLOBPAY = "/glob";
    /**
     * 合同
     */
    public static final String CONTRACT = "/contract";

    /**
     * 申请
     */
    public static final String APPLY = "/apply";


    /**
     * 合同
     */
    public static final String BIND_REMITTANCE_ACCOUNT = "/bindRemittanceAccount";

    /**
     * 发送注册验证码
     */
    public static final String SEND_REGISTER_MESSAGE = "/sendRegisterMessage";

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
    public static final String APP_MODEL = "/appModel";

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
     * 多推-贷超模式-首页
     */
    public static final String MERGE_PUSH_HOME = "/mergePush/home";

    /**
     * 产品列表
     */
    public static final String LIST = "/list";


    /**
     * 产品详情
     */
    public static final String PRODUCT_DETAIL = "/productDetail";

    /**
     * 产品详情
     */
    public static final String DETAIL = "/detail";

    /**
     * 产品详情页
     */
    public static final String VIEW_DETAIL = "/view/detail";

    /**
     * 产品是否续贷开量
     */
    public static final String ISRELOAN = "/isReloan";

    /**
     * 获取产品支付渠道
     */
    public static final String PAY_CHANNEL = "/payChannel";

    /**
     * 多推-贷超模式-首页
     */
    public static final String MERGE_PUSH_LIST = "/mergePush/list";

    /**
     * 多推-变身贷超模式
     */
    public static final String TURN_INTO_LOAN = "/turnIntoLoan";

    /**
     * 忘记密码
     */
    public static final String FORGOT_PWD = "/forgotPwd";

    /**
     * 更新密码
     */
    public static final String MODIFY_PASSWORD = "/modifyPassword";

    /**
     * 修改密码
     */
    public static final String EDIT_PASSWORD = "/editPassword";

    /**
     * 我的个人中心
     */
    public static final String MINE = "/mine";

    /**
     * 新增基本信息
     */
    public static final String USER_INFO_ADD = "/addUserInfo";

    /**
     * 获取基本信息
     */
    public static final String USER_INFO_GET = "/getUserInfo";

    /**
     * 保存个人信息
     */
    public static final String PERSON_INFO_SAVE = "/savePersonalInfo";

    /**
     * 获取个人信息
     */
    public static final String PERSON_INFO_GET = "/getPersonalInfo";

    /**
     * 获取OCR认证聚道
     */
    public static final String OCR_CHANNEL_TYPE = "/ocrChannelType";

    /**
     * 调用advance 获取证件信息接口
     */
    public static final String OCR_ADVANCE_CARD_INFO = "/userCardInfo";

    /**
     * 获取advance license
     */
    public static final String OCR_ADVANCE_LICENSE = "/advanceLicense";

    /**
     * 获取advance 活体识别分
     */
    public static final String OCR_ADVANCE_LIVENESS_SCORE = "/advanceLivenessScore";

    /**
     * 用户OCR识别信息保存
     */
    public static final String SAVE_USER_OCR_INFO = "/saveOcrInfo";

    /**
     * 获取保存的用户OCR信息
     */
    public static final String GET_USER_OCR_INFO = "/getOcrInfo";

    /**
     * 获取银行支行信息
     */
    public static final String BANK_BRANCH = "/bankBranch";

    /**
     * 获取注册页banner
     */
    public static final String REGISTER_BANNER = "/registerBanner";

    /**
     * 获取产品 banner列表
     */
    public static final String PRODUCT_BANNER_LIST = "/bannerList";

    /**
     * 产品 - 推荐列表
     */
    public static final String PRODUCT_RECOMMEND_LIST = "/recommendList";

    /**
     * 订单 - 订单列表
     */
    public static final String ORDER_LIST = "/list";

    /**
     * 订单 - 申请放款
     */
    public static final String APPLYLOAN = "/applyLoan";

    /**
     * 订单 - 申请申请确认页
     */
    public static final String COMFIRM_APPLY = "/comfirmApply";

    /**
     * 多推订单 - 申请确认页
     */
    public static final String CONFIRM_MERGE_PUSH_APPLY = "/confirmMergePushApply";

    /**
     * 订单 - 订单详情
     */
    public static final String ORDER_DETAIL = "/detail";

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
     * uv统计
     */
    public static final String UV = "/uv";


    /**
     * 发起
     */
    public static final String PREPAY = "/prepay";

    /**
     * 保存用户基本信息
     */
    public static final String SAVE_BASIC_INFO = "/basicInfo/save";

    /**
     * 保存用户个人信息
     */
    public static final String SAVE_PERSON_INFO = "/personInfo/save";

    /**
     * 保存用户信息
     */
    public static final String GET_INFO = "/info";
}
