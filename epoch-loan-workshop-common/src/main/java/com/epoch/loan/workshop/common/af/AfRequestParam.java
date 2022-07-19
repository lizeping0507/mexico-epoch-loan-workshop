package com.epoch.loan.workshop.common.af;

import com.epoch.loan.workshop.common.util.DateUtil;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.Calendar;


/**
 * @author ljy
 * @packagename : com.epoch.loan.workshop.common.af
 * @className : AfRequestParam
 * @createTime : 2022/07/18 15:03
 * @Description: 发送af打点事件请求封装类
 */
@Data
@NoArgsConstructor
public class AfRequestParam implements Serializable {

    /**
     * afId
     */
    private String appsflyer_id;

    /**
     * gpsAdId
     */
    private String advertising_id;

    /**
     * 事件名称
     */
    private String eventName;

    /**
     * 事件时间
     */
    private String eventTime;

    public String getEventTime() {
        return eventTime;
    }

    /**
     * 即事件发生的时间，af是以UTC时间显示的
     * @param calendar
     */
    public void setEventTime(Calendar calendar) {
        // 时间偏移量
        int zoneOffset = calendar.get(Calendar.ZONE_OFFSET);
        // 夏令时差
        int dstOffset = calendar.get(Calendar.DST_OFFSET);
        // UTC时间
        calendar.add(Calendar.MILLISECOND,-(zoneOffset + dstOffset));
        this.eventTime = DateUtil.DateToString(calendar.getTime(),"yyyy-MM-dd HH:mm:ss.SSS");
    }
}
