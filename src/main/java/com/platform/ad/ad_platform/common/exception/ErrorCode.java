package com.platform.ad.ad_platform.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 404 Not Found
    COMPANY_NOT_FOUND(HttpStatus.NOT_FOUND, "COMPANY_NOT_FOUND", "회사를 찾을 수 없습니다."),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "PRODUCT_NOT_FOUND", "상품을 찾을 수 없습니다."),
    CONTRACT_NOT_FOUND(HttpStatus.NOT_FOUND, "CONTRACT_NOT_FOUND", "계약을 찾을 수 없습니다."),

    // 400 Bad Request
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "VALIDATION_FAILED", "요청 값이 올바르지 않습니다."),
    INVALID_CONTRACT_PERIOD(HttpStatus.BAD_REQUEST, "INVALID_CONTRACT_PERIOD", "계약 기간은 최소 28일 이상이어야 합니다."),
    INVALID_CONTRACT_AMOUNT(HttpStatus.BAD_REQUEST, "INVALID_CONTRACT_AMOUNT", "계약 금액은 10,000원에서 1,000,000원 범위여야 합니다."),
    INVALID_START_DATE(HttpStatus.BAD_REQUEST, "INVALID_START_DATE", "계약 시작일은 오늘 이후여야 합니다."),
    INVALID_DATE_RANGE(HttpStatus.BAD_REQUEST, "INVALID_DATE_RANGE", "종료일은 시작일보다 이후여야 합니다."),
    CANNOT_CANCEL_CONTRACT(HttpStatus.BAD_REQUEST, "CANNOT_CANCEL_CONTRACT", "집행 전 또는 진행 중인 계약만 취소할 수 있습니다."),

    // 409 Conflict
    DUPLICATE_CONTRACT(HttpStatus.CONFLICT, "DUPLICATE_CONTRACT", "중복된 기간의 계약이 이미 존재합니다."),
    CONTRACT_NUMBER_LIMIT_EXCEEDED(HttpStatus.CONFLICT, "CONTRACT_NUMBER_LIMIT_EXCEEDED", "당일 계약 생성 한도(999건)를 초과하였습니다."),

    // 500 Internal Server Error
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "서버 오류가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
