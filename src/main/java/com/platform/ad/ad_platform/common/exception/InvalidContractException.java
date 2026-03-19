package com.platform.ad.ad_platform.common.exception;

public class InvalidContractException extends BusinessException {

    public InvalidContractException(ErrorCode errorCode) {
        super(errorCode);
    }
}
