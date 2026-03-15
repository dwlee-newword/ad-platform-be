package com.platform.ad.ad_platform.common.exception;

public class ProductNotFoundException extends EntityNotFoundException {

    public ProductNotFoundException() {
        super(ErrorCode.PRODUCT_NOT_FOUND);
    }
}
