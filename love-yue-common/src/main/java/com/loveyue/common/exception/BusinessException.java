package com.loveyue.common.exception;

import com.loveyue.common.enums.ResponseCode;
import lombok.Getter;

import java.io.Serial;

/**
 * @Description: 业务异常类
 * @Date 2025/7/31
 * @Author LoveYue
 */
@Getter
public class BusinessException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -7580822546800389845L;

    private final Integer code;

    private final String message;

    public BusinessException(ResponseCode responseCode) {
        super(responseCode.getMessage());
        this.code = responseCode.getCode();
        this.message = responseCode.getMessage();
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public BusinessException(String message) {
        super(message);
        this.code = ResponseCode.BUSINESS_ERROR.getCode();
        this.message = message;
    }
}
