package com.epoch.loan.workshop.api.controller;

import com.epoch.loan.workshop.common.config.URL;
import com.epoch.loan.workshop.common.constant.ResultEnum;
import com.epoch.loan.workshop.common.params.params.request.CollectionReductionParams;
import com.epoch.loan.workshop.common.params.params.result.Result;
import com.epoch.loan.workshop.common.util.LogUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.api.controller
 * @className : CollectionController
 * @createTime : 2022/3/8 17:18
 * @description : 催收还提
 */


@RestController
@RequestMapping(URL.COLLECTION)
public class CollectionController extends BaseController {

    /**
     * 催收减免
     *
     * @param reductionParams 减免金额相关入参
     * @return
     */
    @PostMapping(URL.REDUCTION)
    public Result<Object> reduction(CollectionReductionParams reductionParams) {
        // 结果集
        Result<Object> result = new Result<>();

        try {
            if (!reductionParams.isOrderNoLegal()) {
                // 异常返回结果
                result.setReturnCode(ResultEnum.PARAM_ERROR.code());
                result.setMessage(ResultEnum.PARAM_ERROR.message());
                return result;
            }
            if (!reductionParams.isOrgIdLegal()) {
                // 异常返回结果
                result.setReturnCode(ResultEnum.PARAM_ERROR.code());
                result.setMessage(ResultEnum.PARAM_ERROR.message());
                return result;
            }
            if (!reductionParams.isSubOverdueAmountLegal()) {
                // 异常返回结果
                result.setReturnCode(ResultEnum.PARAM_ERROR.code());
                result.setMessage(ResultEnum.PARAM_ERROR.message());
                return result;
            }
            return orderService.reduction(reductionParams);
        } catch (Exception e) {
            LogUtil.sysError("[CollectionController reduction]", e);

            // 异常返回结果
            result.setReturnCode(ResultEnum.REDUCTION_AMOUNT_ERROR.code());
            result.setMessage(ResultEnum.REDUCTION_AMOUNT_ERROR.message());
            result.setEx(e.getMessage());
            return result;
        }
    }
}
