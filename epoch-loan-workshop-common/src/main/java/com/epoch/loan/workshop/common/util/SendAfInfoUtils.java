package com.epoch.loan.workshop.common.util;

import com.alibaba.fastjson.JSONObject;
import com.epoch.loan.workshop.common.af.AfRequestParam;
import com.epoch.loan.workshop.common.constant.AppConfigField;
import com.epoch.loan.workshop.common.constant.Field;
import org.apache.commons.lang3.StringUtils;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 魏玉强
 * @packagename : com.epoch.loan.workshop.common.af
 * @className : SendAfInfo
 * @createTime : 2022/07/18 14:11
 * @Description: 发送AF打点事件工具类
 */
public class SendAfInfoUtils {

    /**
     * 发送AF打点事件
     *
     * @param url       af请求地址
     * @param eventName 事件名称
     * @param gaId      谷歌推广id
     * @param afId      AF id
     * @param config    包的相关配置
     * @return
     */
    public static Boolean sendAfEvent(String url, String eventName, String gaId, String afId, String config) {
        try {
            // 校验包的相关配置
            if (StringUtils.isBlank(config)) {
                return false;
            }
            JSONObject jsonObject = JSONObject.parseObject(config);
            String afAppId = jsonObject.getString(AppConfigField.AF_APP_ID);
            String afAppKey = jsonObject.getString(AppConfigField.AF_APP_KEY);
            if (StringUtils.isBlank(afAppId) || StringUtils.isBlank(afAppKey)) {
                return false;
            }

            // 封装 af打点请求参数
            AfRequestParam afRequestParam = new AfRequestParam();
            afRequestParam.setAdvertising_id(gaId);
            afRequestParam.setAppsflyer_id(afId);
            afRequestParam.setEventName(eventName);
            afRequestParam.setEventTime(Calendar.getInstance());
            url = url + afAppId;

            // 封装请求头
            Map<String, String> headers = new HashMap<>(2);
            headers.put(Field.CONTENT_TYPE, HttpUtils.CONTENT_TYPE_JSON);
            headers.put(Field.AUTHENTICATION, afAppKey);

            // 发送请求，获取响应结果
            String response = HttpUtils.POST_WITH_HEADER(url, JSONObject.toJSONString(afRequestParam), headers);

            if (StringUtils.isNotBlank(response)) {
                return true;
            }
            return false;
        } catch (Exception e) {
            LogUtil.sysError("[sendAfEvent]", e);
            // 请求失败
            return false;
        }
    }

}
