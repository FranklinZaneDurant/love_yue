package com.loveyue.common.controller;

import com.loveyue.common.context.RequestContext;
import com.loveyue.common.dto.PageDTO;
import com.loveyue.common.interceptor.RequestContextInterceptor;
import com.loveyue.common.response.BaseResponse;
import com.loveyue.common.response.ListObjectResponse;
import com.loveyue.common.response.ObjectResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @Description: 控制器基类
 * @Date 2025/07/13
 * @Author LoveYue
 */
@Slf4j
@Schema(description = "控制器基类")
public abstract class BaseController {

    /**
     * 获取当前请求上下文
     */
    protected RequestContext getCurrentRequestContext() {
        return RequestContextInterceptor.getCurrentRequestContext();
    }

    /**
     * 获取当前用户ID
     */
    protected Long getCurrentUserId() {
        return getCurrentRequestContext().getUserId();
    }

    /**
     * 获取当前用户名
     */
    protected String getCurrentUsername() {
        return getCurrentRequestContext().getUsername();
    }

    /**
     * 获取请求ID
     */
    protected String getRequestId() {
        RequestContext context = getCurrentRequestContext();
        return context != null ? context.getRequestId() : null;
    }

    /**
     * 成功响应
     */
    protected <T> BaseResponse<T> success() {
        BaseResponse<T> response = BaseResponse.success();
        response.setRequestId(getRequestId());
        return response;
    }

    /**
     * 成功响应带数据
     */
    protected <T> BaseResponse<T> success(T data) {
        BaseResponse<T> response = BaseResponse.success(data);
        response.setRequestId(getRequestId());
        return response;
    }

    /**
     * 成功响应带消息和数据
     */
    protected <T> BaseResponse<T> success(String message, T data) {
        BaseResponse<T> response = BaseResponse.success(message, data);
        response.setRequestId(getRequestId());
        return response;
    }

    /**
     * 单个对象成功响应
     */
    protected <T> ObjectResponse<T> successVO(T data) {
        ObjectResponse<T> response = ObjectResponse.success(data);
        response.setRequestId(getRequestId());
        return response;
    }

    /**
     * 列表成功响应
     */
    protected <T> ListObjectResponse<T> successList(List<T> data) {
        ListObjectResponse<T> response = ListObjectResponse.success(data);
        response.setRequestId(getRequestId());
        return response;
    }

    /**
     * 分页列表成功响应
     */
    protected <T> ListObjectResponse<T> successList(List<T> data, PageDTO pageInfo) {
        ListObjectResponse<T> response = ListObjectResponse.success(data, pageInfo);
        response.setRequestId(getRequestId());
        return response;
    }

    /**
     * 失败响应
     */
    protected <T> BaseResponse<T> error(String message) {
        BaseResponse<T> response = BaseResponse.error(message);
        response.setRequestId(getRequestId());
        return response;
    }

    /**
     * 失败响应带错误码
     */
    protected <T> BaseResponse<T> error(Integer code, String message) {
        BaseResponse<T> response = BaseResponse.error(code, message);
        response.setRequestId(getRequestId());
        return response;
    }

    /**
     * 参数错误响应
     */
    protected <T> BaseResponse<T> badRequest(String message) {
        BaseResponse<T> response = BaseResponse.badRequest(message);
        response.setRequestId(getRequestId());
        return response;
    }

    /**
     * 未授权响应
     */
    protected <T> BaseResponse<T> unauthorized(String message) {
        BaseResponse<T> response = BaseResponse.unauthorized(message);
        response.setRequestId(getRequestId());
        return response;
    }

    /**
     * 禁止访问响应
     */
    protected <T> BaseResponse<T> forbidden(String message) {
        BaseResponse<T> response = BaseResponse.forbidden(message);
        response.setRequestId(getRequestId());
        return response;
    }

    /**
     * 资源未找到响应
     */
    protected <T> BaseResponse<T> notFound(String message) {
        BaseResponse<T> response = BaseResponse.notFound(message);
        response.setRequestId(getRequestId());
        return response;
    }
}