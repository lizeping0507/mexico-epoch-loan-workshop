package com.epoch.loan.workshop.common.util;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.response.OapiRobotSendResponse;
import org.apache.commons.codec.binary.Base64;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * @author 魏玉强
 * @packagename : com.epoch.loan.workshop.common.util
 * @className : sendDingDingUtils
 * @createTime : 2022/03/25 15:25
 * @Description: 发送钉钉信息工具类
 */
public class SendDingDingUtils {

    /**
     * 发送钉钉文本消息
     *
     * @param title      标题
     * @param textStr    消息内容
     * @param isAtAll    是否@所有人
     * @param mobileList 通知具体人的手机号码列表 @指定人
     * @param allWebHook 钉钉自定义机器人webhook + &timestamp=XXX&sign=XXX
     * @return
     */
    public static boolean sendMarkToDingDing(String title, String textStr,
                                             boolean isAtAll, List<String> mobileList, String allWebHook) {
        try {
            DingTalkClient client = new DefaultDingTalkClient(allWebHook);
            OapiRobotSendRequest request = new OapiRobotSendRequest();

            request.setMsgtype("markdown");
            OapiRobotSendRequest.Markdown markdown = new OapiRobotSendRequest.Markdown();
            markdown.setTitle(title);
            markdown.setText(textStr);

            OapiRobotSendRequest.At at = new OapiRobotSendRequest.At();

            if (!StringUtils.isEmpty(isAtAll) && isAtAll) {
                //@所有人
                at.setIsAtAll(true);
            } else if (!CollectionUtils.isEmpty(mobileList)) {
                //@指定人 markdown需要再 text里面加上@人
                StringBuffer phoneStr = new StringBuffer();
                for (String phone : mobileList) {
                    phoneStr.append(" @" + phone);
                }
                markdown.setText(textStr + phoneStr.toString());
                at.setAtMobiles(mobileList);
            }
            request.setMarkdown(markdown);
            request.setAt(at);

            OapiRobotSendResponse response = client.execute(request);
            if (!StringUtils.isEmpty(response)) {
                if (response.getErrcode() == 0 || "ok".equalsIgnoreCase(response.getErrmsg())) {
                    return true;
                }
            }
        } catch (Exception e) {
            LogUtil.sysError("[DingUtils]", e);
        }

        return false;
    }

    /**
     * 获取链接
     *
     * @param webHook 发送地址
     * @param secret  密钥
     * @return
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     * @throws InvalidKeyException
     */
    public static String getAllWebHook(String webHook, String secret)
            throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException {
        Long timestamp = System.currentTimeMillis();

        String stringToSign = timestamp + "\n" + secret;
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256"));
        byte[] signData = mac.doFinal(stringToSign.getBytes("UTF-8"));
        String sign = URLEncoder.encode(new String(Base64.encodeBase64(signData)), "UTF-8");
        //&timestamp=XXX&sign=XXX
        String AllWebHook = webHook + "&&timestamp=" + timestamp + "&sign=" + sign;
        return AllWebHook;
    }
}
