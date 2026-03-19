package com.platform.ad.ad_platform.common.exception;

public class DuplicateContractException extends BusinessException {

    public DuplicateContractException() {
        super(ErrorCode.DUPLICATE_CONTRACT);
    }
}
