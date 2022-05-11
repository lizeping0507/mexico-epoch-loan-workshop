package com.epoch.loan.workshop.common.constant;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.constant
 * @className : CollectionEvent
 * @createTime : 2022/2/26 12:27
 * @description : 催收相关字段定义
 */
public class CollectionField {

    /**
     * 催收事件-案件创建
     */
    public final static int EVENT_CREATE = 0;

    /**
     * 催收事件-案件逾期
     */
    public final static int EVENT_DUE = 1;

    /**
     * 催收事件-案件完结
     */
    public final static int EVENT_COMPLETE = 2;

    /**
     * 催收事件-案件展期
     */
    public final static int EVENT_EXTENSION = 3;

    // ======催收交互相关字段======
    /**
     * 加密key
     */
    public final static String KEY = "key";
    /**
     * 响应码
     */
    public final static String RES_CODE = "code";
    

    // =======催收推送状态定义=======
    /**
     * 推送成功
     */
    public final static Integer PUSH_SUCCESS = 1;
    /**
     * 推送失败
     */
    public final static Integer PUSH_FAILED = 0;

    /**
     * 不推送
     */
    public final static int NO_PUSH = 0 ;
    /**
     * 只推送提还系统
     */
    public final static int PUSH_REMIND = 1 ;
    /**
     * 只推送催收系统
     */
    public final static int PUSH_REACT = 2 ;
    /**
     * 推送提还和催收
     */
    public final static int PUSH_ALL = 3 ;

    /**
     * @Description: 同步催收、提还系统数据类型标志 ：推送用户订单数据
     */
    public final static String PUSH_ORDER="push-order";
    /**
     * @Description: 同步催收、提还系统数据类型标志 ：推送还款数据
     */
    public final static String PUSH_REPAY="push-repay";
    /**
     * @Description: 同步催收、提还系统数据类型标志 ：推送逾期费用数据
     */
    public final static String PUSH_SYNC_OVERDUE="sync-overdue-fee";

    /**
     * 邦地址
     */
    public final static String[] province = {",AGS",",BC",",BCS",",CAM",",CHIS",",CHIH",",COAH",",COL",",CDMX",",DGO",",MEX",",GTO",",GRO",",HGO",",JAL",",MICH",",MOR",",NAY",",NL",",OAX",",PUE",",QRO",",Q ROO",",SLP",",SIN",",SON",",TAB",",TAMPS",",TLAX",",VER",",YUC",",ZAC",};

    /**
     * 催收语言
     */
    public final static String KARNATAKA = "Karnataka";
    public final static String BENGAL = "Bengal";
    public final static String MALAYALAM = "Malayalam";
    public final static String KERALA = "Kerala";
    public final static String BANGALI = "Bangali";
    public final static String TAMIL = "Tamil";
    public final static String KANNAD = "Kannad";
    public final static String TELANGANA = "Telangana";
    public final static String TELUGU = "Telugu";
    public final static String ANDHRAPRADESH= "Andhra Pradesh";

    /**
     * 产品名称
     */
    public final static String SUPERCOIN= "Supercoin";
    public final static String SUPERBOX= "Superbox";
    public final static String FORTUNETREE= "Fortunetree";
    public final static String AC_LOAN= "A&C loan";
    public final static String ACLOAN= "ACloan";

    /**
     * 调用风控拉取原始数的的method
     */
    public final static String COLLECTION_METHOD ="riskmanagement.original.data";

    /**
     * 性别
     */
    public final static String SEX_FEMALE = "Female";
}