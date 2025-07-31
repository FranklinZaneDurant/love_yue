package com.loveyue.common.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description: 响应码枚举类
 * @Date 2025/7/31
 * @Author LoveYue
 */
@Schema(description = "响应码枚举类")
@Getter
@AllArgsConstructor
public enum ResponseCode {

    SUCCESS(200, "操作成功"),
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权访问"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不允许"),
    CONFLICT(409, "资源冲突"),
    VALIDATION_FAILED(422, "参数校验失败"),
    INTERNAL_SERVER_ERROR(500, "服务器内部错误"),
    SERVICE_UNAVAILABLE(503, "服务不可用"),
    BUSINESS_ERROR(6000, "业务处理失败"),
    DATA_NOT_FOUND(6001, "数据不存在"),
    DATA_ALREADY_EXISTS(6002, "数据已存在"),
    OPERATION_NOT_ALLOWED(6003, "操作不被允许");

    private final Integer code;

    private final String message;
}
