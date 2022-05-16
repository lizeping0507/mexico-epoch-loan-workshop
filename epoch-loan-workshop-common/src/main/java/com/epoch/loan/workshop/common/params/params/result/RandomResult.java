package com.epoch.loan.workshop.common.params.params.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA 2020.1
 *
 * @author 魏玉强
 * @version 1.0
 * @date 2022-05-16 15:01
 * @Description: 随机JSON串
 * @program: epoch-loan-workshop
 * @packagename: com.epoch.loan.workshop.common.params.params.result
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RandomResult implements Serializable {

    /**
     * 随机json字符串
     */
    private String json;
}
