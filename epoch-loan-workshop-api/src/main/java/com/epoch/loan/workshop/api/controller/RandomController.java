package com.epoch.loan.workshop.api.controller;

import com.alibaba.fastjson.JSONObject;
import com.epoch.loan.workshop.api.annotated.Authentication;
import com.epoch.loan.workshop.common.config.URL;
import com.epoch.loan.workshop.common.constant.ResultEnum;
import com.epoch.loan.workshop.common.params.params.result.RandomResult;
import com.epoch.loan.workshop.common.params.params.result.Result;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

/**
 * Created with IntelliJ IDEA 2020.1
 *
 * @author 魏玉强
 * @version 1.0
 * @date 2022-05-16 14:58
 * @Description:
 * @program: epoch-loan-workshop
 * @packagename: com.epoch.loan.workshop.api.controller
 */
@RestController
@RequestMapping(URL.RANDOM)
public class RandomController extends BaseController  {

    @Authentication
    @PostMapping(URL.JSONString)
    public Result<RandomResult> randomJson(){
        // 结果集
        Result<RandomResult> result = new Result<>();

        // 响应json类
        JSONObject jsonObject = new JSONObject();

        // json类里面放多少个参数
        Random random = new Random(10);
        int arrNum =random.nextInt(5)+2;

        // 添加随机参数
        for (int i = 0; i < arrNum; i++) {
            jsonObject.put( RandomStringUtils.randomAlphanumeric(10), RandomStringUtils.randomAlphanumeric(10));
        }

        // 封装结果集
        result.setData(new RandomResult(jsonObject.toJSONString()));
        result.setReturnCode(ResultEnum.SUCCESS.code());
        result.setMessage(ResultEnum.SUCCESS.message());
        return result;
    }
}
