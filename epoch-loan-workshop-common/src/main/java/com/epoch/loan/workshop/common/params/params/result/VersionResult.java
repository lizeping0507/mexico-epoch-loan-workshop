package com.epoch.loan.workshop.common.params.params.result;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author 魏玉强
 * @packagename : com.epoch.loan.workshop.common.params.result
 * @className : VersionResult
 * @createTime : 2022/03/29 15:32
 * @Description: 版本控制结果
 */
@Data
public class VersionResult implements Serializable {
    /**
     * 状态 0: 需要更新 1:正常
     */
    private int status;

}
