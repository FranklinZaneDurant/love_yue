package com.loveyue.common.response;

import com.loveyue.common.dto.PageDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.util.List;

/**
 * @Description: 列表响应类
 * @Date 2025/07/13
 * @Author LoveYue
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "列表响应")
public class ListObjectResponse<T> extends BaseResponse<List<T>> {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "分页信息")
    private PageDTO pageInfo;

    public ListObjectResponse() {
        super();
    }

    public ListObjectResponse(Integer code, String message) {
        super(code, message);
    }

    public ListObjectResponse(Integer code, String message, List<T> data) {
        super(code, message, data);
    }

    public ListObjectResponse(Integer code, String message, List<T> data, PageDTO pageInfo) {
        super(code, message, data);
        this.pageInfo = pageInfo;
    }

    /**
     * 成功响应
     */
    public static <T> BaseResponse<T> success() {
        return new ListObjectResponse<>(200, "操作成功");
    }

    /**
     * 成功响应带数据
     */
    public static <T> ListObjectResponse<T> success(List<T> data) {
        return new ListObjectResponse<>(200, "操作成功", data);
    }

    /**
     * 成功响应带数据和分页信息
     */
    public static <T> ListObjectResponse<T> success(List<T> data, PageDTO pageInfo) {
        return new ListObjectResponse<>(200, "操作成功", data, pageInfo);
    }

    /**
     * 成功响应带消息和数据
     */
    public static <T> ListObjectResponse<T> success(String message, List<T> data) {
        return new ListObjectResponse<>(200, message, data);
    }

    /**
     * 成功响应带消息、数据和分页信息
     */
    public static <T> ListObjectResponse<T> success(String message, List<T> data, PageDTO pageInfo) {
        return new ListObjectResponse<>(200, message, data, pageInfo);
    }

    /**
     * 失败响应
     */
    public static <T> ListObjectResponse<T> error(String message) {
        return new ListObjectResponse<>(500, message);
    }

    /**
     * 失败响应带错误码
     */
    public static <T> ListObjectResponse<T> error(Integer code, String message) {
        return new ListObjectResponse<>(code, message);
    }

    /**
     * 参数错误响应
     */
    public static <T> ListObjectResponse<T> badRequest(String message) {
        return new ListObjectResponse<>(400, message);
    }

    /**
     * 未授权响应
     */
    public static <T> ListObjectResponse<T> unauthorized(String message) {
        return new ListObjectResponse<>(401, message);
    }

    /**
     * 禁止访问响应
     */
    public static <T> ListObjectResponse<T> forbidden(String message) {
        return new ListObjectResponse<>(403, message);
    }

    /**
     * 资源未找到响应
     */
    public static <T> ListObjectResponse<T> notFound(String message) {
        return new ListObjectResponse<>(404, message);
    }
}