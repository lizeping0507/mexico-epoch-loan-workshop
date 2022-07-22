package com.epoch.loan.workshop.api.controller;

import com.alibaba.fastjson.JSON;
import com.epoch.loan.workshop.common.config.URL;
import com.epoch.loan.workshop.common.entity.mysql.LoanUserEntity;
import com.epoch.loan.workshop.common.params.params.request.AfCallBackParams;
import com.epoch.loan.workshop.common.util.LogUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 魏玉强
 * @packagename : com.epoch.loan.workshop.api.controller
 * @className : AfCallBackController
 * @createTime : 2022/07/22 18:57
 * @Description:
 */
@RestController
@RequestMapping(URL.AF)
public class AfCallBackController extends BaseController {

    public ResponseEntity<?> afCallBack(@RequestBody AfCallBackParams params) {
        LogUtil.sysInfo("af 回传信息：{}", JSON.toJSONString(params));

        // 响应结果
        ResponseEntity<Object> response = new ResponseEntity<>(HttpStatus.OK);

        if (ObjectUtils.isEmpty(params)) {
            return response;
        }

        LoanUserEntity loanUserEntity = null;
        if (StringUtils.isNotBlank(params.getAdvertisingId()) ) {

        }




        return response;
    }
}
