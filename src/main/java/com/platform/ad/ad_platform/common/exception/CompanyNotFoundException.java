package com.platform.ad.ad_platform.common.exception;

public class CompanyNotFoundException extends EntityNotFoundException {

    public CompanyNotFoundException() {
        super(ErrorCode.COMPANY_NOT_FOUND);
    }
}
