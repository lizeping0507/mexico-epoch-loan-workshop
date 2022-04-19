package com.epoch.loan.workshop.common.params.advance.userocrauth;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created with IntelliJ IDEA 2020.1
 *
 * @author 魏玉强
 * @version 1.0
 * @date 2022-02-09 14:13
 * @Description: advance 返回app端 识别ocr信息
 * @program: platform
 * @packagename: com.yinda.platform.dao.user.dto.advance.userocrauth
 */
@Setter
@Getter
@ToString
public class UserOcrAuthResponse {
    private String info;
    /**
     * 识别类型1 aadhar正面 2 aadhar背面 3 pan卡正面
     */
    private Integer type;
}
