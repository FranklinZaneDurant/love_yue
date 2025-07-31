package com.loveyue.common.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Description: 响应基类
 * @Date 2025/07/13
 * @Author LoveYue
 */
@Data
@Schema(description = "响应基类")
public class BaseResponse<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "响应码")
    private Integer code;

    @Schema(description = "响应消息")
    private String message;

    @Schema(description = "响应数据")
    private T data;

    @Schema(description = "时间戳")
    private Long timestamp;

    @Schema(description = "请求ID")
    private String requestId;

    @Schema(description = "是否成功")
    private Boolean success;

    public BaseResponse() {
        this.timestamp = System.currentTimeMillis();
    }

    public BaseResponse(Integer code, String message) {
        this();
        this.code = code;
        this.message = message;
        this.success = code == 200;
    }

    public BaseResponse(Integer code, String message, T data) {
        this(code, message);
        this.data = data;
    }

    /**
     * 成功响应
     */
    public static <T> BaseResponse<T> success() {
        return new BaseResponse<>(200, "操作成功");
    }

    /**
     * 成功响应带数据
     */
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(200, "操作成功", data);
    }

    /**
     * 成功响应带消息和数据
     */
    public static <T> BaseResponse<T> success(String message, T data) {
        return new BaseResponse<>(200, message, data);
    }

    /**
     * 失败响应
     */
    public static <T> BaseResponse<T> error(String message) {
        return new BaseResponse<>(500, message);
    }

    /**
     * 失败响应带错误码
     */
    public static <T> BaseResponse<T> error(Integer code, String message) {
        return new BaseResponse<>(code, message);
    }

    /**
     * 参数错误响应
     */
    public static <T> BaseResponse<T> badRequest(String message) {
        return new BaseResponse<>(400, message);
    }

    /**
     * 未授权响应
     */
    public static <T> BaseResponse<T> unauthorized(String message) {
        return new BaseResponse<>(401, message);
    }

    /**
     * 禁止访问响应
     */
    public static <T> BaseResponse<T> forbidden(String message) {
        return new BaseResponse<>(403, message);
    }

    /**
     * 资源未找到响应
     */
    public static <T> BaseResponse<T> notFound(String message) {
        return new BaseResponse<>(404, message);
    }
}