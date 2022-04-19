package com.epoch.loan.workshop.api.controller;

import com.epoch.loan.workshop.api.annotated.Authentication;
import com.epoch.loan.workshop.common.config.URL;
import com.epoch.loan.workshop.common.constant.ResultEnum;
import com.epoch.loan.workshop.common.params.params.BaseParams;
import com.epoch.loan.workshop.common.params.params.request.*;
import com.epoch.loan.workshop.common.params.params.result.*;
import com.epoch.loan.workshop.common.util.LogUtil;
import com.epoch.loan.workshop.common.util.ThrowableUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author 魏玉强 身份证
 * @packagename : com.epoch.loan.workshop.api.controller
 * @className : IdCardController
 * @createTime : 2022/03/28 15:23
 * @Description: OCR信息接口
 */
@RestController
@RequestMapping(URL.OCR)
public class OcrController extends BaseController {

    /**
     * 获取用户OCR认证提供商
     *
     * @param mineParams 查询OCR提供商封装类
     * @return 本次使用哪个OCR提供商
     */
    @Authentication
    @PostMapping(URL.OCR_CHANNEL_TYPE)
    public Result<ChannelTypeResult> getOcrChannelType(MineParams mineParams) {
        // 结果集
        Result<ChannelTypeResult> result = new Result<>();

        try {
            // 获取用户OCR认证提供商
            return ocrService.getOcrChannelType(mineParams);
        } catch (Exception e) {
            LogUtil.sysError("[OcrController getOcrChannelType]", e);

            // 异常返回结果
            result.setEx(ThrowableUtils.throwableToString(e));
            result.setReturnCode(ResultEnum.SYSTEM_ERROR.code());
            result.setMessage(ResultEnum.SYSTEM_ERROR.message());
            return result;
        }
    }

    /**
     * 获取advance的license
     *
     * @param params license请求参数
     * @return advance的license
     */
    @Authentication
    @PostMapping(URL.OCR_ADVANCE_LICENSE)
    public Result<LicenseResult> advanceLicense(BaseParams params) {
        // 结果集
        Result<LicenseResult> result = new Result<>();

        try {
            if (params.isAppNameLegal()) {
                // 异常返回结果
                result.setReturnCode(ResultEnum.PARAM_ERROR.code());
                result.setMessage(ResultEnum.PARAM_ERROR.message() + ":appName");
                return result;
            }

            // 获取用户OCR认证提供商
            return ocrService.advanceLicense(params);
        } catch (Exception e) {
            LogUtil.sysError("[OcrController advanceLicense]", e);

            // 异常返回结果
            result.setEx(ThrowableUtils.throwableToString(e));
            result.setReturnCode(ResultEnum.SYSTEM_ERROR.code());
            result.setMessage(ResultEnum.SYSTEM_ERROR.message());
            return result;
        }
    }

    /**
     * advance ：活体识别，获取活体分，并进行判断是否需要重新做活体
     *
     * @param params advance获取活体分封装类
     * @return 查询活体分结果
     */
    @PostMapping(URL.OCR_ADVANCE_LIVENESS_SCORE)
    public Result<UserLivenessScoreResult> advanceLivenessScore(UserLivenessScoreParams params) {
        // 结果集
        Result<UserLivenessScoreResult> result = new Result<>();

        try {
            // 获取用户OCR认证提供商
            return ocrService.advanceLivenessScore(params);
        } catch (Exception e) {
            LogUtil.sysError("[OcrController advanceLivenessScore]", e);

            // 异常返回结果
            result.setEx(ThrowableUtils.throwableToString(e));
            result.setReturnCode(ResultEnum.SYSTEM_ERROR.code());
            result.setMessage(ResultEnum.SYSTEM_ERROR.message());
            return result;
        }
    }

    /**
     * 判断并保存用户OCR识别记录
     *
     * @param params OCR识别结果信息
     * @return 识别是否通过
     */
    @PostMapping(URL.OCR_USER_FACE_MATCH)
    public Result<Object> userFaceMatch(UserFaceMatchParams params) {
        // 结果集
        Result<Object> result = new Result<>();

        try {
            // 获取用户OCR认证提供商
            return userService.userFaceMatch(params);
        } catch (Exception e) {
            LogUtil.sysError("[OcrController userFaceMatch]", e);

            // 异常返回结果
            result.setEx(ThrowableUtils.throwableToString(e));
            result.setReturnCode(ResultEnum.SYSTEM_ERROR.code());
            result.setMessage(ResultEnum.SYSTEM_ERROR.message());
            return result;
        }
    }

    /**
     * 用户OCR识别信息保存
     *
     * @param params 保存用户OCR识别信息请求参数封装类
     * @return 保存成功与否
     */
    @PostMapping(URL.SAVE_USER_OCR_INFO)
    public Result<Object> saveOcrInfo(UserOcrInfoParams params) {
        // 结果集
        Result<Object> result = new Result<>();

        try {
            // 获取用户OCR认证提供商
            return userService.saveOcrInfo(params);
        } catch (Exception e) {
            LogUtil.sysError("[OcrController saveOcrInfo]", e);

            // 异常返回结果
            result.setEx(ThrowableUtils.throwableToString(e));
            result.setReturnCode(ResultEnum.SYSTEM_ERROR.code());
            result.setMessage(ResultEnum.SYSTEM_ERROR.message());
            return result;
        }
    }

    /**
     * 获取用户OCR保存信息
     *
     * @param params 获取用户OCR保存信息请求参数封装类
     * @return 保存成功与否
     */
    @PostMapping(URL.GET_USER_OCR_INFO)
    public Result<UserOcrBasicInfoResult> getOcrInfo(MineParams params) {
        // 结果集
        Result<UserOcrBasicInfoResult> result = new Result<>();

        try {
            // 获取用户OCR认证提供商
            return userService.getOcrInfo(params);
        } catch (Exception e) {
            LogUtil.sysError("[OcrController getOcrInfo]", e);

            // 异常返回结果
            result.setEx(ThrowableUtils.throwableToString(e));
            result.setReturnCode(ResultEnum.SYSTEM_ERROR.code());
            result.setMessage(ResultEnum.SYSTEM_ERROR.message());
            return result;
        }
    }

    /**
     * ocr识别信息保存接口
     *
     * @param params 获取用户OCR保存信息请求参数封装类
     * @return 保存成功与否
     */
    @PostMapping(URL.SAVE_FILE)
    public Result<Object> uploadS3Images(UploadS3Params params, HttpServletRequest request) {
        // 结果集
        Result<Object> result = new Result<>();

        try {
            MultipartResolver resolver = new StandardServletMultipartResolver();
            MultipartHttpServletRequest mRequest = resolver.resolveMultipart(request);
            Map<String, MultipartFile> fileMap = mRequest.getFileMap();

            params.setPanImgData(fileMap.get("panImg").getBytes());
            params.setLivingImgData(fileMap.get("livingImg").getBytes());
            params.setFrontImgData(fileMap.get("frontImg").getBytes());
            params.setBackImgData(fileMap.get("backImg").getBytes());

            // 证件上传
            return userService.uploadS3Images(params);
        } catch (Exception e) {
            LogUtil.sysError("[UserController uploadS3Images]", e);

            // 异常返回结果
            result.setEx(ThrowableUtils.throwableToString(e));
            result.setReturnCode(ResultEnum.SYSTEM_ERROR.code());
            result.setMessage(ResultEnum.SYSTEM_ERROR.message());
            return result;
        }
    }

    /**
     * 获取证件和人脸相似度
     *
     * @param params 获取人脸相似度信息请求参数封装类
     * @return 人脸相似度信息
     */
    @PostMapping(URL.FACE_COMPARISON)
    public Result<UserFaceComparisonResult> faceComparison(UserFaceComparisonParams params, HttpServletRequest request) {
        // 结果集
        Result<UserFaceComparisonResult> result = new Result<>();

        try {
            MultipartResolver resolver = new StandardServletMultipartResolver();
            MultipartHttpServletRequest mRequest = resolver.resolveMultipart(request);
            Map<String, MultipartFile> fileMap = mRequest.getFileMap();

            params.setIdImageData(fileMap.get("idImage").getBytes());
            params.setFaceImageData(fileMap.get("faceImage").getBytes());

            // 获取相似度
            return userService.faceComparison(params);
        } catch (Exception e) {
            LogUtil.sysError("[UserController faceComparison]", e);

            // 异常返回结果
            result.setEx(ThrowableUtils.throwableToString(e));
            result.setReturnCode(ResultEnum.SYSTEM_ERROR.code());
            result.setMessage(ResultEnum.SYSTEM_ERROR.message());
            return result;
        }
    }


    /**
     * advance获取证件信息
     *
     * @param params 获取证件信息请求参数封装类
     * @return 证件信息
     */
    @PostMapping(URL.USER_OCR_INFO)
    public Result<UserOcrResult> userOcrInfo(UserOcrFullInfoParams params, HttpServletRequest request) {
        // 结果集
        Result<UserOcrResult> result = new Result<>();

        try {
            MultipartResolver resolver = new StandardServletMultipartResolver();
            MultipartHttpServletRequest mRequest = resolver.resolveMultipart(request);
            Map<String, MultipartFile> fileMap = mRequest.getFileMap();

            params.setImageData(fileMap.get("image").getBytes());

            // 获取证件信息
            return userService.userOcrInfo(params);
        } catch (Exception e) {
            LogUtil.sysError("[UserController faceComparison]", e);

            // 异常返回结果
            result.setEx(ThrowableUtils.throwableToString(e));
            result.setReturnCode(ResultEnum.SYSTEM_ERROR.code());
            result.setMessage(ResultEnum.SYSTEM_ERROR.message());
            return result;
        }
    }
}
