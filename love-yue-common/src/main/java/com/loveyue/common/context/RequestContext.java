package com.loveyue.common.context;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 请求上下文传输类
 * @Date 2025/07/13
 * @Author LoveYue
 */
@Data
@Schema(description = "请求上下文传输类")
public class RequestContext implements Serializable {
    @Serial
    private static final long serialVersionUID = -7732289244541764022L;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "请求ID")
    private String requestId;

    @Schema(description = "请求时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date requestTime;

    @Schema(description = "客户端IP")
    private String clientIp;

    @Schema(description = "用户代理")
    private String userAgent;

    @Schema(description = "请求路径")
    private String requestPath;

    @Schema(description = "请求方法")
    private String requestMethod;

    @Schema(description = "角色列表")
    private String roles;

    @Schema(description = "权限列表")
    private String permissions;

    public RequestContext() {
        this.requestTime = new Date();
    }
}