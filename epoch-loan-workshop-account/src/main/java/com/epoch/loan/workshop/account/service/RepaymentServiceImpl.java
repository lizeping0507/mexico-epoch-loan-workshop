package com.epoch.loan.workshop.account.service;

import com.alibaba.fastjson.JSONObject;
import com.epoch.loan.workshop.account.SpringContext;
import com.epoch.loan.workshop.account.repayment.BaseRepayment;
import com.epoch.loan.workshop.common.constant.LoanRepaymentPaymentRecordStatus;
import com.epoch.loan.workshop.common.constant.PayStrategy;
import com.epoch.loan.workshop.common.constant.PlatformUrl;
import com.epoch.loan.workshop.common.constant.ResultEnum;
import com.epoch.loan.workshop.common.entity.mysql.*;
import com.epoch.loan.workshop.common.params.params.request.*;
import com.epoch.loan.workshop.common.params.params.result.PandaPayH5Result;
import com.epoch.loan.workshop.common.params.params.result.Result;
import com.epoch.loan.workshop.common.service.RepaymentService;
import com.epoch.loan.workshop.common.util.HttpUtils;
import com.epoch.loan.workshop.common.util.LogUtil;
import com.epoch.loan.workshop.common.util.ObjectIdUtil;
import com.epoch.loan.workshop.common.util.PlatformUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Value;

import java.util.*;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.account.service;
 * @className : RepaymentServiceImpl
 * @createTime : 2022/3/5 11:50
 * @description : 还款
 */
@DubboService(timeout = 5000)
public class RepaymentServiceImpl extends BaseService implements RepaymentService {
    @Value("repayment.errorPage")
    String errorPageUrl;

    @Override
    public String repayment(PreRepaymentParams params) {
        LogUtil.sysInfo("params : {}", JSONObject.toJSONString(params));
        // 查询还款记录
        String orderBillId = params.getId();
        LoanOrderBillEntity orderBill = loanOrderBillDao.findOrderBill(orderBillId);
        LogUtil.sysInfo("orderBill : {}", JSONObject.toJSONString(orderBill));
        LoanOrderEntity order = loanOrderDao.findOrder(orderBill.getOrderId());
        LogUtil.sysInfo("order : {}", JSONObject.toJSONString(order));


        // 查询挑选策略
        LoanProductRepaymentConfigEntity config = loanProductRepaymentConfigDao.findByGroupName(order.getRepaymentDistributionGroup());
        if (config == null) {
            return errorPageUrl;
        }
        LogUtil.sysInfo("config:{}", config);

        // 查询支付分配列表
        List<LoanRepaymentDistributionEntity> loanRepaymentDistributions = loanRepaymentDistributionDao.findRepaymentDistribution(config.getStrategyName());
        LogUtil.sysInfo("loanRepaymentDistributions:{}", loanRepaymentDistributions);

        // 挑选渠道放款
        return repayment(loanRepaymentDistributions, config, orderBill, params);
    }

    /**
     * 挑选渠道放款 (有递归)
     *
     * @param loanRepaymentDistributions 渠道配置列表
     * @param config                     挑选策略
     * @param orderBill                  订单记录
     * @return 跳转页面
     */
    private String repayment(List<LoanRepaymentDistributionEntity> loanRepaymentDistributions,
                             LoanProductRepaymentConfigEntity config,
                             LoanOrderBillEntity orderBill,
                             PreRepaymentParams params) {
        LogUtil.sysInfo("repayment start ... ");

        String payUrl = null;

        LogUtil.sysInfo("repayment loanRepaymentDistributions:{}", loanRepaymentDistributions);

        // 备选渠道判空
        if (CollectionUtils.isEmpty(loanRepaymentDistributions)) {
            LogUtil.sysInfo("无可用渠道配置:{}", loanRepaymentDistributions);
            //  无可用渠道配置
            return errorPageUrl;
        }

        // 根据策略挑选渠道
        LoanRepaymentDistributionEntity selectedRepaymentDistribution;
        if (config.getStrategyName().equals(PayStrategy.WEIGHT)) {
            // 根据权重策略选择渠道
            selectedRepaymentDistribution = chooseByWeight(loanRepaymentDistributions);
            LogUtil.sysInfo("根据权重策略选择渠道:{}", JSONObject.toJSONString(selectedRepaymentDistribution));

            if (ObjectUtils.isEmpty(selectedRepaymentDistribution)) {
                // 无可用渠道配置
                LogUtil.sysInfo("根据权重策略选择渠道:{}", "空=====");
                return errorPageUrl;
            }

            // 权重挑选渠道
            String paymentId = selectedRepaymentDistribution.getPaymentId();

            // 订单信息
            LoanOrderEntity order = loanOrderDao.findOrder(orderBill.getOrderId());
            LogUtil.sysInfo("order:{}", order);

            // 用户信息
            LoanUserEntity user = loanUserDao.findById(order.getUserId());
            LogUtil.sysInfo("user:{}", user);

            // 用户基本信息
            LoanUserInfoEntity userBasicInfo = loanUserInfoDao.findUserInfoById(order.getUserId());
            LogUtil.sysInfo("userBasicInfo:{}", userBasicInfo);

            // 用户放款账户信息
            LoanRemittanceAccountEntity remittanceAccount = loanRemittanceAccountDao.findRemittanceAccount(order.getBankCardId());
            LogUtil.sysInfo("remittanceAccount:{}", remittanceAccount);

            // 创建订单详情记录
            LoanRepaymentPaymentRecordEntity loanRepaymentPaymentRecordEntity = new LoanRepaymentPaymentRecordEntity();
            String paymentLogId = ObjectIdUtil.getObjectId();
            loanRepaymentPaymentRecordEntity.setId(paymentLogId);
            loanRepaymentPaymentRecordEntity.setStatus(LoanRepaymentPaymentRecordStatus.CREATE);
            loanRepaymentPaymentRecordEntity.setPaymentId(paymentId);
            loanRepaymentPaymentRecordEntity.setCreateTime(new Date());
            loanRepaymentPaymentRecordEntity.setUpdateTime(new Date());
            loanRepaymentPaymentRecordEntity.setOrderId(orderBill.getOrderId());
            loanRepaymentPaymentRecordEntity.setOrderBillId(orderBill.getId());
            loanRepaymentPaymentRecordEntity.setAmount(orderBill.getRepaymentAmount() - orderBill.getReceivedAmount());
            loanRepaymentPaymentRecordEntity.setActualAmount(0D);
            loanRepaymentPaymentRecordEntity.setPhone(userBasicInfo.getMobile());
            loanRepaymentPaymentRecordEntity.setEmail(userBasicInfo.getEmail());
            loanRepaymentPaymentRecordEntity.setName(remittanceAccount.getName());
            loanRepaymentPaymentRecordEntity.setEvent("OrderCompere");
            LogUtil.sysInfo("loanRepaymentPaymentRecordEntity:{}", loanRepaymentPaymentRecordEntity);

            loanRepaymentPaymentRecordDao.insert(loanRepaymentPaymentRecordEntity);
            LogUtil.sysInfo("存储:{}", loanRepaymentPaymentRecordEntity);


            // 渠道信息
            LoanPaymentEntity payment = loanPaymentDao.getById(paymentId);

            // 策略模式发起放款
            // 根据名称获取发起还款的实例方法
            BaseRepayment repaymentUtil = SpringContext.getBean(payment.getName(), BaseRepayment.class);
            LogUtil.sysInfo("repaymentUtil:{}", repaymentUtil);

            // 发起还款
            payUrl = repaymentUtil.startRepayment(loanRepaymentPaymentRecordEntity, payment, params);
            LogUtil.sysInfo("payUrl:{}", payUrl);
        } else {
            // 策略无效
            return errorPageUrl;
        }

        // 发起结果校验
        if (StringUtils.isEmpty(payUrl)) {
            // 放款失败 递归:重新挑选渠道放款
            LogUtil.sysInfo("payUrl:{} 放款失败 递归重新挑选渠道放款", payUrl);
            loanRepaymentDistributions.remove(selectedRepaymentDistribution);
            payUrl = repayment(loanRepaymentDistributions, config, orderBill, params);
        }

        return payUrl;
    }

    /**
     * 根据权重 递归 挑选渠道
     *
     * @param loanRepaymentDistributions 渠道权重列表
     * @return 渠道配置
     */
    private LoanRepaymentDistributionEntity chooseByWeight(List<LoanRepaymentDistributionEntity> loanRepaymentDistributions) {
        LoanRepaymentDistributionEntity res = null;

        // 查询渠道列表
        // 随机范围 = 渠道权重和
        int range = loanRepaymentDistributions.stream().mapToInt(LoanRepaymentDistributionEntity::getProportion).sum();
        LogUtil.sysInfo("range:{}", range);
        if (range == 0) {
            return null;
        }

        // 取随机数
        Random random = new Random();
        int randomNum = random.nextInt(range) + 1;
        ;
        LogUtil.sysInfo("randomNum:{}", randomNum);

        // 选择渠道
        int start = 0;
        for (LoanRepaymentDistributionEntity entity : loanRepaymentDistributions) {
            Integer proportion = entity.getProportion();
            if (randomNum > start && randomNum <= (start + proportion)) {
                res = entity;
                break;
            } else {
                start += proportion;
            }
        }
        LogUtil.sysInfo("res:{}", res);

        // 为空直接返回空
        if (ObjectUtils.isEmpty(res)) {
            return res;
        }

        // 不为空进行渠道状态校验
        String paymentId = res.getPaymentId();
        LoanPaymentEntity loanPayment = loanPaymentDao.getById(paymentId);
        if (ObjectUtils.isEmpty(loanPayment) || loanPayment.getStatus() != 1) {
            // 渠道无效 递归挑选
            loanRepaymentDistributions.remove(res);
            res = chooseByWeight(loanRepaymentDistributions);
        }

        return res;
    }

    /**
     * 回调处理
     *
     * @param params 回调参数
     * @return String
     */
    @Override
    public String pandaPay(PandaRepaymentCallbackParam params) {
        LoanRepaymentPaymentRecordEntity paymentRecord = loanRepaymentPaymentRecordDao.findRepaymentPaymentRecordById(params.getId());
        LoanPaymentEntity paymentEntity = loanPaymentDao.getById(paymentRecord.getPaymentId());

        com.epoch.loan.workshop.common.mq.repayment.params.RepaymentParams repaymentParams = new com.epoch.loan.workshop.common.mq.repayment.params.RepaymentParams();
        repaymentParams.setId(paymentRecord.getId());
        try {
            repaymentMQManager.sendMessage(repaymentParams, paymentEntity.getName());
            return "success";
        } catch (Exception exception) {
            exception.printStackTrace();
            return "failed";
        }
    }

    /**
     * 调支付提供的UTR接口
     *
     * @param params utr参数
     * @return 调用支付成功与否
     * @throws Exception 请求异常
     */
    @Override
    public Result<Object> repayUtr(UtrParams params) throws Exception {
        // 结果集
        Result<Object> result = new Result<>();

        // 拼接请求路径
        String url = platformConfig.getPlatformDomain() + PlatformUrl.PLATFORM_REPAY_UTR;

        // 封装请求参数
        JSONObject requestParam = new JSONObject();
        requestParam.put("appFlag", params.getAppName());
        requestParam.put("versionNumber", params.getAppVersion());
        requestParam.put("mobileType", params.getMobileType());
        requestParam.put("orderNo", params.getOrderNo());
        requestParam.put("utr", params.getUtr());

        // 封装请求头
        Map<String, String> headers = new HashMap<>();
        headers.put("token", params.getToken());

        // 请求
        String responseStr = HttpUtils.POST_WITH_HEADER(url, requestParam.toJSONString(), headers);
        LogUtil.sysInfo("url : {} \n request: {} \n responseStr: {}", url, requestParam.toJSONString(), responseStr);

        // 解析响应结果
        JSONObject responseJson = JSONObject.parseObject(responseStr);

        // 判断接口响应是否正常
        if (!PlatformUtil.checkResponseCode(result, Object.class, responseJson)) {
            return result;
        }

        // 封装结果
        result.setReturnCode(ResultEnum.SUCCESS.code());
        result.setMessage(ResultEnum.SUCCESS.message());
        return result;
    }

    /**
     * pandaPay OXXO方式H5回调
     *
     * @param params
     * @return
     */
    @Override
    public Result<Object> pandaPayH5(PandaPayH5Params params){
        Result<Object> result = new Result<>();

        String id = params.getId();
        LoanRepaymentPaymentRecordEntity record = loanRepaymentPaymentRecordDao.findRepaymentPaymentRecordById(id);
        // TODO 结果校验

        String clabe = record.getClabe();
        String barCode = record.getBarCode();
        Double amount = record.getAmount();

        // 拆分
        List<String> spiltCodes = new ArrayList<>();
        int length = clabe.length();
        int start = 0;
        while (start <= length - 3) {
            spiltCodes.add(clabe.substring(start, start + 3));
            start += 3;
        }
        spiltCodes.add(clabe.substring(start));


        PandaPayH5Result data = new PandaPayH5Result();
        data.setSpiltCode(spiltCodes);
        data.setClabe(clabe);
        data.setBarCode(barCode);
        data.setAmount(amount.toString());
        result.setData(data);

        return result;
    }
}
