package com.platform.ad.ad_platform.common.response;

import com.platform.ad.ad_platform.common.exception.ErrorCode;
import lombok.Getter;

import java.util.List;

@Getter
public class ErrorResponse {

    private final String code;
    private final String message;
    private final List<String> details;

    private ErrorResponse(String code, String message, List<String> details) {
        this.code = code;
        this.message = message;
        this.details = details;
    }

    public static ErrorResponse of(ErrorCode errorCode) {
        return new ErrorResponse(errorCode.getCode(), errorCode.getMessage(), List.of());
    }

    public static ErrorResponse of(ErrorCode errorCode, List<String> details) {
        return new ErrorResponse(errorCode.getCode(), errorCode.getMessage(), details);
    }
}
