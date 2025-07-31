package com.loveyue.common.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * @Description: 单个对象响应类
 * @Date 2025/07/13
 * @Author LoveYue
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "单个对象响应")
public class ObjectResponse<T> extends BaseResponse<T> {
    @Serial
    private static final long serialVersionUID = 1L;

    public ObjectResponse() {
        super();
    }

    public ObjectResponse(Integer code, String message) {
        super(code, message);
    }

    public ObjectResponse(Integer code, String message, T data) {
        super(code, message, data);
    }

    /**
     * 成功响应
     */
    public static <T> ObjectResponse<T> success() {
        return new ObjectResponse<>(200, "操作成功");
    }

    /**
     * 成功响应带数据
     */
    public static <T> ObjectResponse<T> success(T data) {
        return new ObjectResponse<>(200, "操作成功", data);
    }

    /**
     * 成功响应带消息和数据
     */
    public static <T> ObjectResponse<T> success(String message, T data) {
        return new ObjectResponse<>(200, message, data);
    }

    /**
     * 失败响应
     */
    public static <T> ObjectResponse<T> error(String message) {
        return new ObjectResponse<>(500, message);
    }

    /**
     * 失败响应带错误码
     */
    public static <T> ObjectResponse<T> error(Integer code, String message) {
        return new ObjectResponse<>(code, message);
    }

    /**
     * 参数错误响应
     */
    public static <T> ObjectResponse<T> badRequest(String message) {
        return new ObjectResponse<>(400, message);
    }

    /**
     * 未授权响应
     */
    public static <T> ObjectResponse<T> unauthorized(String message) {
        return new ObjectResponse<>(401, message);
    }

    /**
     * 禁止访问响应
     */
    public static <T> ObjectResponse<T> forbidden(String message) {
        return new ObjectResponse<>(403, message);
    }

    /**
     * 资源未找到响应
     */
    public static <T> ObjectResponse<T> notFound(String message) {
        return new ObjectResponse<>(404, message);
    }
}