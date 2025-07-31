package com.loveyue.common.utils;

import cn.hutool.core.lang.UUID;
import com.loveyue.common.dto.BaseDTO;
import com.loveyue.common.dto.PageResponseDTO;
import com.loveyue.common.enums.ResponseCode;
import com.loveyue.common.response.ListResponse;
import com.loveyue.common.response.ObjectResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

/**
 * @Description: 响应工具类
 * @Date 2025/7/31
 * @Author LoveYue
 */
public class ResponseUtils {
    private static final String REQUEST_ID_HEADER = "X-Request_ID";

    private ResponseUtils() {
    }

    /**
     * 从 HTTP 请求头中获取当前请求 ID。如果请求 ID 不存在或为空，则生成一个新的唯一标识符（UUID）并返回。
     *
     * @return 当前请求 ID，或者如果在标头中未找到有效的请求 ID，则生成一个新的 UUID
     */
    public static String getCurrentRequestId() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String requestId = request.getHeader(REQUEST_ID_HEADER);
        if (requestId == null || requestId.trim().isEmpty()) {
            requestId = UUID.randomUUID().toString();
        }
        return requestId;
    }

    /**
     * 创建一个表示成功的响应对象。
     *
     * @param <T>  泛型参数，必须是BaseDTO或其子类的实例
     * @param data 作为响应体的数据
     * @return 包含成功状态码和给定数据的ObjectResponse对象
     */
    public static <T extends BaseDTO> ObjectResponse<T> success(T data) {
        return buildResponse(ResponseCode.SUCCESS, data);
    }

    /**
     * 创建一个表示成功的响应对象，不携带数据。
     *
     * @param <T> 泛型参数，必须是BaseDTO或其子类的实例
     * @return 包含成功状态码但无数据的ObjectResponse对象
     */
    public static <T extends BaseDTO> ObjectResponse<T> success() {
        return buildResponse(ResponseCode.SUCCESS, null);
    }

    /**
     * 创建一个表示错误的响应对象，不携带数据。
     *
     * @param <T>          泛型参数，必须是BaseDTO或其子类的实例
     * @param responseCode 响应码枚举，用于设置响应状态码及消息
     * @return 包含给定响应码但无数据的ObjectResponse对象
     */
    public static <T extends BaseDTO> ObjectResponse<T> error(ResponseCode responseCode) {
        return buildResponse(responseCode, null);
    }

    /**
     * 创建一个表示错误的响应对象。
     *
     * @param <T>     泛型参数，必须是BaseDTO或其子类的实例
     * @param code    错误代码
     * @param message 错误消息
     * @return 包含给定错误代码和消息的ObjectResponse对象
     */
    public static <T extends BaseDTO> ObjectResponse<T> error(Integer code, String message) {
        return buildResponse(code, message, false, null);
    }

    /**
     * 创建一个表示成功的列表响应对象。
     *
     * @param <T>  泛型参数，必须是BaseDTO或其子类的实例
     * @param data 作为响应体的数据列表
     * @return 包含成功状态码和给定数据列表的ListResponse对象
     */
    public static <T extends BaseDTO> ListResponse<T> successList(List<T> data) {
        return buildListResponse(data);
    }

    /**
     * 创建一个表示成功的分页响应对象。
     *
     * @param <T>             泛型参数，必须是BaseDTO或其子类的实例
     * @param pageResponseDTO 分页信息数据传输对象
     * @param data            作为响应体的数据列表
     * @return 包含成功状态码、给定数据列表和分页信息的ListResponse对象
     */
    public static <T extends BaseDTO> ListResponse<T> successPage(PageResponseDTO pageResponseDTO, List<T> data) {
        ListResponse<T> listResponse = new ListResponse<>();
        listResponse.setData(data);

        PageResponseDTO pageInfo = new PageResponseDTO();
        pageInfo.setPageNo(pageResponseDTO.getPageNo());
        pageInfo.setPageSize(pageResponseDTO.getPageSize());
        pageInfo.setTotal(pageResponseDTO.getTotal());
        pageInfo.setTotalPages(pageResponseDTO.getTotalPages());
        pageInfo.setHasNext(pageResponseDTO.getHasNext());
        pageInfo.setHasPrevious(pageResponseDTO.getHasPrevious());

        listResponse.setPage(pageInfo);
        return listResponse;
    }

    /**
     * 构建一个带有指定响应码和数据的响应对象。
     *
     * @param responseCode 响应码枚举，用于设置响应状态码及消息
     * @param data         作为响应体的数据，必须是BaseDTO或其子类的实例
     * @return 包含给定响应码和数据的ObjectResponse对象
     */
    private static <T extends BaseDTO> ObjectResponse<T> buildResponse(ResponseCode responseCode, T data) {
        return buildResponse(responseCode.getCode(), responseCode.getMessage(),
                responseCode == ResponseCode.SUCCESS, data);
    }

    /**
     * 构建响应对象的通用方法
     *
     * @param code    响应代码
     * @param message 响应消息
     * @param success 是否成功
     * @param data    响应数据
     * @return 构建好的ObjectResponse对象
     */
    private static <T extends BaseDTO> ObjectResponse<T> buildResponse(Integer code, String message,
                                                                       Boolean success, T data) {
        ObjectResponse<T> response = new ObjectResponse<>();
        response.setCode(code);
        response.setMessage(message);
        response.setSuccess(success);
        response.setTimestamp(System.currentTimeMillis());
        response.setRequestId(getCurrentRequestId());
        response.setData(data);

        return response;
    }

    /**
     * 构建一个带有指定响应码和数据列表的ListResponse对象。
     *
     * @param <T>  泛型参数，必须是BaseDTO或其子类的实例
     * @param data 作为响应体的数据列表
     * @return 包含给定响应码和数据列表的ListResponse对象
     */
    private static <T extends BaseDTO> ListResponse<T> buildListResponse(List<T> data) {
        ListResponse<T> response = new ListResponse<>();
        response.setCode(ResponseCode.SUCCESS.getCode());
        response.setMessage(ResponseCode.SUCCESS.getMessage());
        response.setSuccess(true);
        response.setTimestamp(System.currentTimeMillis());
        response.setRequestId(getCurrentRequestId());
        response.setData(data);

        return response;
    }
}
