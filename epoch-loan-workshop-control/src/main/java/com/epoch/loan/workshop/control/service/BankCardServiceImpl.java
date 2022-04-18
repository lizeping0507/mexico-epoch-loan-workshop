package com.epoch.loan.workshop.control.service;


import com.alibaba.fastjson.JSONObject;
import com.epoch.loan.workshop.common.constant.PlatformUrl;
import com.epoch.loan.workshop.common.constant.ResultEnum;
import com.epoch.loan.workshop.common.params.params.request.BankCardParams;
import com.epoch.loan.workshop.common.params.params.request.BindBankCardParams;
import com.epoch.loan.workshop.common.params.params.request.ParentParams;
import com.epoch.loan.workshop.common.params.params.result.*;
import com.epoch.loan.workshop.common.service.BankCardService;
import com.epoch.loan.workshop.common.util.HttpUtils;
import com.epoch.loan.workshop.common.util.PlatformUtil;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : ljy
 * @packageName : com.epoch.loan.workshop.control.service
 * @className : BankCardServiceImpl
 * @createTime : 22/3/29 11:08
 * @description : 银行卡相关业务
 */
@DubboService(timeout = 5000)
public class BankCardServiceImpl extends BaseService implements BankCardService {

    /**
     * 获取银行支行信息
     *
     * @param params 支行上级封装类
     * @return 子集支行集合
     * @throws Exception 请求异常
     */
    @Override
    public Result<ListResult> getBankBranch(ParentParams params) throws Exception {
        // 结果集
        Result<ListResult> result = new Result<>();

        // 拼接请求路径
        String url = platformConfig.getPlatformDomain() + PlatformUrl.PLATFORM_BANK_BRANCH;

        // 封装请求参数
        JSONObject requestParam = new JSONObject();
        requestParam.put("appFlag", params.getAppName());
        requestParam.put("versionNumber", params.getAppVersion());
        requestParam.put("mobileType", params.getMobileType());
        requestParam.put("parentId", params.getParentId());

        // 封装请求头
        Map<String, String> headers = new HashMap<>();
        headers.put("token", params.getToken());

        // 请求
        String responseStr = HttpUtils.POST_WITH_HEADER(url, requestParam.toJSONString(), headers);

        // 解析响应结果
        JSONObject responseJson = JSONObject.parseObject(responseStr);

        // 判断接口响应是否正常
        if (!PlatformUtil.checkResponseCode(result, ListResult.class, responseJson)) {
            return result;
        }

        // 获取结果集
        JSONObject data = responseJson.getJSONObject("data");

        // 封装结果就
        ListResult listResult = JSONObject.parseObject(data.toJSONString(), ListResult.class);

        // 封装结果
        result.setReturnCode(ResultEnum.SUCCESS.code());
        result.setMessage(ResultEnum.SUCCESS.message());
        result.setData(listResult);
        return result;
    }

    /**
     * 银行卡列表
     *
     * @param params 入参
     * @return 银行卡集合
     * @throws Exception 请求异常
     */
    @Override
    public Result<BankCardResult> bankCardList(BankCardParams params) throws Exception {
        // 结果集
        Result<BankCardResult> result = new Result<>();

        // 拼接请求路径
        String url = platformConfig.getPlatformDomain() + PlatformUrl.PLATFORM_BANK_CARD_LIST;

        // 封装请求参数
        JSONObject requestParam = new JSONObject();
        requestParam.put("userId", params.getUserId());
        requestParam.put("orderNo", params.getOrderNo());
        requestParam.put("appType", params.getAppType());
        requestParam.put("mobileType", params.getMobileType());
        requestParam.put("appFlag", params.getAppName());
        requestParam.put("versionNumber", params.getAppVersion());

        // 封装请求头
        Map<String, String> headers = new HashMap<>();
        headers.put("token", params.getToken());

        // 请求
        String responseStr = HttpUtils.POST_WITH_HEADER(url, requestParam.toJSONString(), headers);

        // 解析响应结果
        JSONObject responseJson = JSONObject.parseObject(responseStr);

        // 判断接口响应是否正常
        if (!PlatformUtil.checkResponseCode(result, BankCardResult.class, responseJson)) {
            return result;
        }

        // 获取结果集
        JSONObject data = responseJson.getJSONObject("data");

        // 转换封装结果集
        BankCardResult bankCardResult = JSONObject.parseObject(data.toJSONString(), BankCardResult.class);

        // 封装结果
        result.setReturnCode(ResultEnum.SUCCESS.code());
        result.setMessage(ResultEnum.SUCCESS.message());
        result.setData(bankCardResult);
        return result;
    }

    /**
     * 绑新卡
     *
     * @param params 入参
     * @return 是否绑卡成功
     * @throws Exception 请求异常
     */
    @Override
    public Result<BindBankCardResult> bindNewBankCard(BindBankCardParams params) throws Exception {
        //结果集
        Result<BindBankCardResult> result = new Result<>();

        // 拼接请求路径
        String url = platformConfig.getPlatformDomain() + PlatformUrl.PLATFORM_BIND_NEW_BANK_CARD;

        // 封装请求参数
        JSONObject requestParam = new JSONObject();
        requestParam.put("orderNo", params.getOrderNo());
        requestParam.put("bankCard", params.getBankCard());
        requestParam.put("openBank", params.getOpenBank());
        requestParam.put("userName", params.getUserName());
        requestParam.put("idNumber", params.getIdNumber());
        requestParam.put("userMobile", params.getUserMobile());
        requestParam.put("bankAddress", params.getBankAddress());
        requestParam.put("appType", params.getAppType());
        requestParam.put("mobileType", params.getMobileType());
        requestParam.put("appFlag", params.getAppName());
        requestParam.put("versionNumber", params.getAppVersion());

        // 封装请求头
        Map<String, String> headers = new HashMap<>();
        headers.put("token", params.getToken());

        // 请求
        String responseStr = HttpUtils.POST_WITH_HEADER(url, requestParam.toJSONString(), headers);

        // 解析响应结果
        JSONObject responseJson = JSONObject.parseObject(responseStr);

        // 判断接口响应是否正常
        if (!PlatformUtil.checkResponseCode(result, BindBankCardResult.class, responseJson)) {
            return result;
        }

        // 获取结果集
        JSONObject data = responseJson.getJSONObject("data");

        // 转换封装结果集
        BindBankCardResult newBankCardResult = JSONObject.parseObject(data.toJSONString(), BindBankCardResult.class);

        // 封装结果
        result.setReturnCode(ResultEnum.SUCCESS.code());
        result.setMessage(ResultEnum.SUCCESS.message());
        result.setData(newBankCardResult);
        return result;
    }

    /**
     * 确认旧卡
     *
     * @param params 入参
     * @return 是否绑卡成功
     * @throws Exception 请求异常
     */
    @Override
    public Result<BindBankCardResult> confirmOldBankCard(BindBankCardParams params) throws Exception {
        //结果集
        Result<BindBankCardResult> result = new Result<>();

        // 拼接请求路径
        String url = platformConfig.getPlatformDomain() + PlatformUrl.PLATFORM_CONFIRM_OLD_BANK_CARD;

        // 封装请求参数
        JSONObject requestParam = new JSONObject();
        requestParam.put("orderNo", params.getOrderNo());
        requestParam.put("bankCard", params.getBankCard());
        requestParam.put("openBank", params.getOpenBank());
        requestParam.put("userName", params.getUserName());
        requestParam.put("idNumber", params.getIdNumber());
        requestParam.put("userMobile", params.getUserMobile());
        requestParam.put("bankAddress", params.getBankAddress());
        requestParam.put("appFlag", params.getAppName());
        requestParam.put("versionNumber", params.getAppVersion());
        requestParam.put("appType", params.getAppType());

        // 封装请求头
        Map<String, String> headers = new HashMap<>();
        headers.put("token", params.getToken());

        // 请求
        String responseStr = HttpUtils.POST_WITH_HEADER(url, requestParam.toJSONString(), headers);

        // 解析响应结果
        JSONObject responseJson = JSONObject.parseObject(responseStr);

        // 判断接口响应是否正常
        if (!PlatformUtil.checkResponseCode(result, BindBankCardResult.class, responseJson)) {
            return result;
        }

        // 获取结果集
        JSONObject data = responseJson.getJSONObject("data");

        // 转换封装结果集
        BindBankCardResult newBankCardResult = JSONObject.parseObject(data.toJSONString(), BindBankCardResult.class);

        // 封装结果
        result.setReturnCode(ResultEnum.SUCCESS.code());
        result.setMessage(ResultEnum.SUCCESS.message());
        result.setData(newBankCardResult);
        return result;
    }

    /**
     * 确认旧卡并领款
     *
     * @param params 绑卡参数
     * @return 是否绑定成功
     * @throws Exception 请求异常
     */
    @Override
    public Result<BindBankCardResult> confirmOldBankCardAndLoanAmount(BindBankCardParams params) throws Exception {
        //结果集
        Result<BindBankCardResult> result = new Result<>();

        // 拼接请求路径
        String url = platformConfig.getPlatformDomain() + PlatformUrl.PLATFORM_CONFIRM_OLD_BANK_CARD_LOAN;

        // 封装请求参数
        JSONObject requestParam = new JSONObject();
        requestParam.put("orderNo", params.getOrderNo());
        requestParam.put("bankCard", params.getBankCard());
        requestParam.put("openBank", params.getOpenBank());
        requestParam.put("userName", params.getUserName());
        requestParam.put("idNumber", params.getIdNumber());
        requestParam.put("userMobile", params.getUserMobile());
        requestParam.put("bankAddress", params.getBankAddress());
        requestParam.put("appFlag", params.getAppName());
        requestParam.put("versionNumber", params.getAppVersion());
        requestParam.put("appType", params.getAppType());
        requestParam.put("mobileType", params.getMobileType());

        // 封装请求头
        Map<String, String> headers = new HashMap<>();
        headers.put("token", params.getToken());

        // 请求
        String responseStr = HttpUtils.POST_WITH_HEADER(url, requestParam.toJSONString(), headers);

        // 解析响应结果
        JSONObject responseJson = JSONObject.parseObject(responseStr);

        // 判断接口响应是否正常
        if (!PlatformUtil.checkResponseCode(result, BindBankCardResult.class, responseJson)) {
            return result;
        }

        // 获取结果集
        JSONObject data = responseJson.getJSONObject("data");

        // 转换封装结果集
        BindBankCardResult newBankCardResult = JSONObject.parseObject(data.toJSONString(), BindBankCardResult.class);

        // 封装结果
        result.setReturnCode(ResultEnum.SUCCESS.code());
        result.setMessage(ResultEnum.SUCCESS.message());
        result.setData(newBankCardResult);
        return result;
    }

    /**
     * 新增银行卡
     *
     * @param params 银行卡参数
     * @return 银行卡信息
     * @throws Exception 请求异常
     */
    @Override
    public Result<AddBankcardResult> addBankcard(BindBankCardParams params) throws Exception {
        //结果集
        Result<AddBankcardResult> result = new Result<>();

        // 拼接请求路径
        String url = platformConfig.getPlatformDomain() + PlatformUrl.PLATFORM_ADD_BANK_CARD;

        // 封装请求参数
        JSONObject requestParam = new JSONObject();
        requestParam.put("orderNo", params.getOrderNo());
        requestParam.put("bankCard", params.getBankCard());
        requestParam.put("openBank", params.getOpenBank());
        requestParam.put("userName", params.getUserName());
        requestParam.put("idNumber", params.getIdNumber());
        requestParam.put("userMobile", params.getUserMobile());
        requestParam.put("bankAddress", params.getBankAddress());
        requestParam.put("appFlag", params.getAppName());
        requestParam.put("versionNumber", params.getAppVersion());
        requestParam.put("appType", params.getAppType());
        requestParam.put("mobileType", params.getMobileType());

        // 封装请求头
        Map<String, String> headers = new HashMap<>();
        headers.put("token", params.getToken());

        // 请求
        String responseStr = HttpUtils.POST_WITH_HEADER(url, requestParam.toJSONString(), headers);

        // 解析响应结果
        JSONObject responseJson = JSONObject.parseObject(responseStr);

        // 判断接口响应是否正常
        if (!PlatformUtil.checkResponseCode(result, AddBankcardResult.class, responseJson)) {
            return result;
        }

        // 获取结果集
        JSONObject data = responseJson.getJSONObject("data");

        // 转换封装结果集
        AddBankcardResult addBankcardResult = JSONObject.parseObject(data.toJSONString(), AddBankcardResult.class);

        // 封装结果
        result.setReturnCode(ResultEnum.SUCCESS.code());
        result.setMessage(ResultEnum.SUCCESS.message());
        result.setData(addBankcardResult);
        return result;
    }

}
