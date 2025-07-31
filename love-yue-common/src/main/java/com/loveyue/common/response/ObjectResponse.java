package com.loveyue.common.response;

import com.loveyue.common.dto.BaseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * @Description: 单个对象响应类
 * @Date 2025/7/31
 * @Author LoveYue
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "统一响应类")
public class ObjectResponse<T extends BaseDTO> extends BaseResponse {
    @Serial
    private static final long serialVersionUID = -7548879882080646927L;

    @Schema(description = "响应数据")
    private T data;
}
