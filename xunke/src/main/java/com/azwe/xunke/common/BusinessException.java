package com.azwe.xunke.common;

/**
 * Created by AZ
 */

public class BusinessException extends Exception {
    private CommonError commonError;

    public BusinessException(EmBusinessError emBusinessError) {
        super();
        this.commonError = new CommonError(emBusinessError);
    }
}
