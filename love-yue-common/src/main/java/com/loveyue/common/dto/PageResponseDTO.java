package com.loveyue.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Description: 分页响应数据传输类
 * @Date 2025/7/31
 * @Author LoveYue
 */
@Data
@Schema(description = "分页响应数据传输类")
public class PageResponseDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 7335296411769576778L;

    @Schema(description = "页号")
    private Integer pageNo;

    @Schema(description = "分页大小")
    private Integer pageSize;

    @Schema(description = "总记录数")
    private Long total;

    @Schema(description = "总页数")
    private Integer totalPages;

    @Schema(description = "是否有下一页")
    private Boolean hasNext;

    @Schema(description = "是否有上一页")
    private Boolean hasPrevious;
}
