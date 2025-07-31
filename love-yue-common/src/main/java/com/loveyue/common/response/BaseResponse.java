package com.loveyue.common.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Description: 响应基类
 * @Date 2025/7/31
 * @Author LoveYue
 */
@Data
public abstract class BaseResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 8493878373124806227L;

    @Schema(description = "响应码")
    private Integer code;

    @Schema(description = "响应消息")
    private String message;

    @Schema(description = "时间戳")
    private Long timestamp;

    @Schema(description = "请求Id")
    private String requestId;

    @Schema(description = "是否成功")
    private Boolean success;
}
