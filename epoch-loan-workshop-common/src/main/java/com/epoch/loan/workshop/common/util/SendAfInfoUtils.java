package com.epoch.loan.workshop.common.util;

import com.alibaba.fastjson.JSONObject;
import com.epoch.loan.workshop.common.af.AfRequestParam;
import com.epoch.loan.workshop.common.constant.Field;
import org.apache.commons.lang3.StringUtils;

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
     * @param url        af请求地址
     * @param afRequestParam 打点事件信息封装
     * @param appId      包名 例如：com.app.rupayekey
     * @param afAppKey   af打点用的app_key
     * @return
     */
    public static Boolean sendAfEvent(String url, AfRequestParam afRequestParam, String appId, String afAppKey) {
        try {
            url = url + appId;

            // 封装请求头
            Map<String, String> headers = new HashMap<>(2);
            headers.put(Field.CONTENT_TYPE,HttpUtils.CONTENT_TYPE_JSON);
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
