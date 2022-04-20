package com.epoch.loan.workshop.common.service;

import com.epoch.loan.workshop.common.params.params.BaseParams;
import com.epoch.loan.workshop.common.params.params.request.UserLivenessScoreParams;
import com.epoch.loan.workshop.common.params.params.result.ChannelTypeResult;
import com.epoch.loan.workshop.common.params.params.result.LicenseResult;
import com.epoch.loan.workshop.common.params.params.result.Result;
import com.epoch.loan.workshop.common.params.params.result.UserLivenessScoreResult;

/**
 * @author 魏玉强
 * @packagename : com.epoch.loan.workshop.common.service
 * @className : IdCardService
 * @createTime : 2022/03/28 15:36
 * @Description: OCR认证
 */
public interface OcrService {

    /**
     * 获取用户OCR认证提供商
     *
     * @param mineParams 查询OCR提供商封装类
     * @return 本次使用哪个OCR提供商
     * @throws Exception
     */
    Result<ChannelTypeResult> getOcrChannelType(BaseParams mineParams);

    /**
     * 获取advance的license
     *
     * @param params license请求参数
     * @return advance的license
     * @throws Exception
     */
    Result<LicenseResult> advanceLicense(BaseParams params) throws Exception;

    /**
     * advance ：活体识别，获取活体分，并进行判断是否需要重新做活体
     *
     * @param params advance获取活体分封装类
     * @return 查询活体分结果
     * @throws Exception
     */
    Result<UserLivenessScoreResult> advanceLivenessScore(UserLivenessScoreParams params) throws Exception;
}
