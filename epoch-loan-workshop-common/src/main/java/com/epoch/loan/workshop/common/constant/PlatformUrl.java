package com.epoch.loan.workshop.common.constant;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.constant;
 * @className : PlatformUrl
 * @createTime : 2022/3/21 15:00
 * @description : 贷超V1访问地址
 */
public class PlatformUrl {

    // ============== 用户相关 ===================
    /**
     * 判断手机号是否已经注册
     */
    public final static String PLATFORM_ISREGISTER = "/api/v1/u/isRegister/";

    /**
     * 密码登录
     */
    public final static String PLATFORM_PWREGISTER = "/api/v1/u/pwRegister/";

    /**
     * 用户注册
     */
    public final static String PLATFORM_REGISTER = "/api/v1/u/register/";

    /**
     * 发送注册短信
     */
    public final static String PLATFORM_SMSCODE = "/api/v1/u/smsCode";

    /**
     * 忘记密码
     */
    public final static String PLATFORM_FORGOTPWD = "/api/v1/u/forgotPwd";

    /**
     * 更新密码
     */
    public final static String PLATFORM_MODIFYPASSWWORD = "/api/v1/u/modifyPassword";

    /**
     * 修改密码
     */
    public final static String PLATFORM_EDITPASSWWORD = "/api/v1/u/editPassword/";

    /**
     * 我的个人中心
     */
    public final static String PLATFORM_MINE = "/api/v1/userInfo/mine/";

    /**
     * 新增基本信息
     */
    public static final String PLATFORM_USER_INFO_ADD = "/api/v1/userInfo/add";

    /**
     * 获取基本信息
     */
    public static final String PLATFORM_USER_INFO_GET = "/api/v1/userInfo/getByProductId";

    /**
     * 新增/更新个人信息
     */
    public static final String PLATFORM_PERSON_INFO_SAVE = "/api/v1/userInfo/savePersonalInfo";

    /**
     * 获取个人信息
     */
    public static final String PLATFORM_PERSON_INFO = "/api/v1/userInfo/getPersonalInfo";


    // ================ 产品相关 ===================

    /**
     * 判断用户App模式
     */
    public final static String PLATFORM_GETUSERAPPMODEL = "/api/v1/product/getUserAppModel";

    /**
     * 多推-贷超模式-首页
     */
    public final static String PLATFORM_MERGE_PUSH_HOME = "/api/v1/product/mergePushHome";

    /**
     * 多推-贷超模式-产品列表
     */
    public final static String PLATFORM_MERGE_PUSH_LIST = "/api/v1/product/mergePushProductList";

    /**
     * 多推-变身贷超
     */
    public final static String PLATFORM_TURN_INFO_LOAN = "/api/v1//product/turnIntoLoan";

    /**
     * 产品列表
     */
    public final static String PLATFORM_PRODUCT_LIST = "/api/v1/product/userList";

    /**
     * 产品详情
     */
    public final static String PLATFORM_PRODUCT_DETAIL = "/api/v1/product/productDetail";

    /**
     * 产品详情（产品详情页中调用
     */
    public final static String PLATFORM_PRODUCT_VIEW_DETAIL = "/api/v1/product/viewProduct";

    /**
     * 产品是否续贷开量
     */
    public final static String PLATFORM_PRODUCT_ISRELOAN = "/api/v1/product/isReloanProduct";

    /**
     * 获取产品的支付渠道
     */
    public final static String PLATFORM_PRODUCT_PAYCHANNEL = "/api/v1/product/getProductPayChannel";

    /**
     * 获取注册页banner
     */
    public final static String PLATFORM_PRODUCT_BANNER = "/api/v1/product/findRegisterBanner";

    /**
     * 获取产品 banner列表
     */
    public final static String PLATFORM_PRODUCT_BANNER_LIST = "/api/v1/product/getBanner";

    /**
     * 获取产品推荐列表
     */
    public final static String PLATFORM_PRODUCT_RECOMMEND_LIST = "/api/v1/product/recommendList";

    /**
     * uv统计
     */
    public final static String PLATFORM_PRODUCT_UV = "/api/v1/product/countView";

    // ================ 订单相关 ===================

    /**
     * 订单列表
     */
    public final static String PLATFORM_ORDER_LIST = "/api/v1/order/getOrderList";

    /**
     * 申请放款
     */
    public final static String PLATFORM_APPLYLOAN = "/api/v1/order/applyLoan";

    /**
     * 申请确认页
     */
    public final static String PLATFORM_COMFIRM_APPLY = "/api/v1/order/comfirmApply";

    /**
     * 多推 -- 申请确认页
     */
    public final static String PLATFORM_CONFIRM_MERGE_PUSH_APPLY = "/api/v1/product/comfirmMergePushApply";


    // ================ 银行卡相关 ===================

    /**
     * 获取银行支行信息
     */
    public final static String PLATFORM_BANK_BRANCH = "/api/v1/order/bankBranch";

    /**
     * 银行卡列表
     */
    public final static String PLATFORM_BANK_CARD_LIST = "/api/v1/order/getBankCardList";

    /**
     * 绑新卡
     */
    public final static String PLATFORM_BIND_NEW_BANK_CARD = "/api/v1/order/bindNewBankCard";

    /**
     * 绑旧卡
     */
    public final static String PLATFORM_CONFIRM_OLD_BANK_CARD = "/api/v1/order/bindExistedBankCard";

    /**
     * 新增银行卡
     */
    public final static String PLATFORM_ADD_BANK_CARD = "/api/v1/order/addBankcard";

    /**
     * 确认旧卡并领款
     */
    public final static String PLATFORM_CONFIRM_OLD_BANK_CARD_LOAN = "/api/v1/order/comfirmBankcardApproval";

    // ================ 支付相关 ===================

    /**
     * 支付侧UTR
     */
    public final static String PLATFORM_REPAY_UTR = "/api/v1/order/repayUTR";

    // ================ SDK相关 ===================

    /**
     * SDK上传同步回调
     */
    public final static String PLATFORM_SDK_REPORT_SYNC = "/api/v1/order/sdkReportSync";

    /**
     * 是否推送基本信息
     */
    public final static String PLATFORM_SDK_PUSH_DETAIL_INFO = "/api/v1/order/isPushDetailInfo";
}
