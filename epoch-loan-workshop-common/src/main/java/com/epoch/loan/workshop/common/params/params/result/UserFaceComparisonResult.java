package com.epoch.loan.workshop.common.params.params.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author 魏玉强
 * @packagename : com.epoch.loan.workshop.common.params.params.result
 * @className : UserFaceComparisonResult
 * @createTime : 2022/04/21 16:04
 * @Description: 证件和人脸相似度响应前端结果封装
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserFaceComparisonResult implements Serializable {

    /**
     * 相似度值
     */
    private String similarity;
}
