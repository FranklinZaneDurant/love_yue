package com.loveyue.common.response;

import com.loveyue.common.dto.BaseDTO;
import com.loveyue.common.dto.PageResponseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.util.List;

/**
 * @Description: 列表响应类
 * @Date 2025/7/31
 * @Author LoveYue
 */
@Data
@Schema(description = "列表响应类")
public class ListResponse<T extends BaseDTO> extends BaseResponse {
    @Serial
    private static final long serialVersionUID = 6823595726867160988L;

    private PageResponseDTO page;

    private List<T> data;
}
