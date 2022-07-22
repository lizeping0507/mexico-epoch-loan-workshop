package com.epoch.loan.workshop.common.af;

import com.alibaba.fastjson.JSONObject;
import com.epoch.loan.workshop.common.constant.AppConfigField;
import com.epoch.loan.workshop.common.constant.Field;
import com.epoch.loan.workshop.common.util.DateUtil;
import com.epoch.loan.workshop.common.util.HttpUtils;
import com.epoch.loan.workshop.common.util.LogUtil;
import com.google.common.collect.Maps;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 魏玉强
 * @packagename : com.epoch.loan.workshop.common.af
 * @className : LoanAfClient
 * @createTime : 2022/07/22 17:25
 * @Description:
 */
@RefreshScope
@Component
@Data
public class LoanAfClient {

    /**
     * af相关配置
     */
    @Autowired
    AfConfig afConfig;

    /**
     * 发送AF打点事件
     *
     * @param eventName 事件名称
     * @param gaId      谷歌推广id
     * @param afId      AF id
     * @param config    包的相关配置
     * @return
     */
    public Boolean sendAfEvent(String eventName, String gaId, String afId, String config) {
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
            Map<String,Object> afRequestParam = Maps.newHashMap();
            afRequestParam.put("advertising_id",gaId);
            afRequestParam.put("advertising_id",afId);
            afRequestParam.put("eventName",eventName);

            Calendar calendar = Calendar.getInstance();

            // 时间偏移量
            int zoneOffset = calendar.get(Calendar.ZONE_OFFSET);

            // 夏令时差
            int dstOffset = calendar.get(Calendar.DST_OFFSET);

            // UTC时间
            calendar.add(Calendar.MILLISECOND,-(zoneOffset + dstOffset));

            // 即事件发生的时间，af是以UTC时间显示的
            afRequestParam.put("eventTime", DateUtil.DateToString(calendar.getTime(),"yyyy-MM-dd HH:mm:ss.SSS"));
            String url = afConfig.getAfRequesterUrl() + afAppId;

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
