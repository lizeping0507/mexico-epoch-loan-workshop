package com.epoch.loan.workshop.common.params.advance.face;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserFaceComparisonResult implements Serializable {

    private String code;

    private String message;

    private String transactionId;

    private String pricingStrategy;

    private String extra;

    private String similarity;

}
