package com.epoch.loan.workshop.common.service;

import com.epoch.loan.workshop.common.params.params.request.AfCallBackParams;
import com.epoch.loan.workshop.common.params.params.result.Result;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author 魏玉强
 * @packagename : com.epoch.loan.workshop.common.service
 * @className : AfCallBackService
 * @createTime : 2022/07/23 10:51
 * @Description: af 回调
 */
public interface AfCallBackService {

    /**
     * af 回调业务处理
     *
     * @param params af回调参数
     * @return 响应结果
     */
    Result afCallBack(@RequestBody AfCallBackParams params);
}
