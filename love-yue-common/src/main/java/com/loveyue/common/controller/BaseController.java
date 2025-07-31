package com.loveyue.common.controller;

import com.loveyue.common.context.RequestContext;
import com.loveyue.common.dto.BaseDTO;
import com.loveyue.common.dto.PageDTO;
import com.loveyue.common.dto.PageResponseDTO;
import com.loveyue.common.response.ListResponse;
import com.loveyue.common.response.ObjectResponse;
import com.loveyue.common.utils.ResponseUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

/**
 * @Description: 控制器基类
 * @Date 2025/7/31
 * @Author LoveYue
 */
public abstract class BaseController {
    private static final Logger logger = LoggerFactory.getLogger(BaseController.class);

    protected HttpServletRequest request;

    protected HttpServletResponse response;

    protected RequestContext requestContext;

    /**
     * 初始化控制器的请求和响应上下文。
     * 这个方法在任何处理程序方法被执行之前被自动调用，为传入的HTTP请求设置必要的上下文。
     *
     * @param request  包含客户端请求的HttpServletRequest对象
     * @param response HttpServletResponse对象，该对象将用于将响应发送回客户机
     */
    @ModelAttribute
    public void initRequestContext(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
        this.requestContext = buildRequestContext();

        logger.info("Request started: {} {} from {}",
                request.getMethod(),
                request.getRequestURI(),
                getClientIpAddress(request));
    }

    /**
     * 构建请求上下文对象，该对象包含处理HTTP请求所需的关键信息。
     *
     * @return 返回一个填充了请求ID、客户端IP、用户代理、请求路径及请求方法的RequestContext实例
     */
    private RequestContext buildRequestContext() {
        RequestContext context = new RequestContext();
        context.setRequestId(ResponseUtils.getCurrentRequestId());
        context.setClientIp(getClientIpAddress(request));
        context.setUserAgent(request.getHeader("User-Agent"));
        context.setRequestPath(request.getRequestURI());
        context.setRequestMethod(request.getMethod());

        // TODO 这里可以从JWT token或者session中获取用户信息

        return context;
    }

    /**
     * 获取客户端IP地址。
     * <p>
     * 该方法优先从"X-Forwarded-For"请求头中获取客户端IP地址，如果不存在或为空，则尝试从"X-Real-Ip"请求头中获取。
     * 若上述两个请求头均未能提供有效的客户端IP地址，则使用HttpServletRequest的getRemoteHost()方法返回的值。
     *
     * @param request 包含客户端请求信息的HttpServletRequest对象
     * @return 客户端的IP地址字符串
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equals(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-Ip");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equals(xRealIp)) {
            return xRealIp;
        }

        return request.getRemoteHost();
    }

    protected <T extends BaseDTO> ObjectResponse<T> success(T data) {
        return ResponseUtils.success(data);
    }

    protected <T extends BaseDTO> ObjectResponse<T> success() {
        return ResponseUtils.success();
    }

    protected <T extends BaseDTO> ListResponse<T> success(List<T> data) {
        return ResponseUtils.successList(data);
    }

    protected <T extends BaseDTO> ListResponse<T> success(PageResponseDTO pageResponseDTO, List<T> data) {
        return ResponseUtils.successPage(pageResponseDTO, data);
    }

    protected Long getCurrentUserId() {
        return requestContext != null ? requestContext.getUserId() : null;
    }

    protected String getCurrentRequestId() {
        return requestContext != null ? requestContext.getRequestId() : null;
    }

    protected void validatePageParams(PageDTO pageDTO) {
        if (pageDTO == null) {
            return;
        }

        if (pageDTO.getPageNo() < 1) {
            pageDTO.setPageNo(1);
        }

        if (pageDTO.getPageSize() < 1) {
            pageDTO.setPageSize(10);
        }

        if (pageDTO.getPageSize() > 1000) {
            pageDTO.setPageSize(1000);
        }
    }
}
