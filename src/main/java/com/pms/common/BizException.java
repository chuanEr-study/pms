package com.pms.common;

import org.springframework.http.HttpStatus;

/**
 * 业务异常：携带 HTTP 状态码与业务码，由全局异常处理器翻译成统一响应结构。
 */
public class BizException extends RuntimeException {

    private final HttpStatus httpStatus;

    private final String code;

    public BizException(HttpStatus httpStatus, String code, String message) {
        super(message);
        this.httpStatus = httpStatus;
        this.code = code;
    }

    public static BizException badRequest(String code, String message) {
        return new BizException(HttpStatus.BAD_REQUEST, code, message);
    }

    public static BizException unauthorized(String code, String message) {
        return new BizException(HttpStatus.UNAUTHORIZED, code, message);
    }

    public static BizException notFound(String code, String message) {
        return new BizException(HttpStatus.NOT_FOUND, code, message);
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getCode() {
        return code;
    }
}
