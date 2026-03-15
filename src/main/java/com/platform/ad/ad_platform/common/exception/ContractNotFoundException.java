package com.platform.ad.ad_platform.common.exception;

public class ContractNotFoundException extends EntityNotFoundException {

    public ContractNotFoundException() {
        super(ErrorCode.CONTRACT_NOT_FOUND);
    }
}
