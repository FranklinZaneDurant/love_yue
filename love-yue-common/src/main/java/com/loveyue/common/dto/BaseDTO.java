package com.loveyue.common.dto;

import com.loveyue.common.enums.EntityStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 数据传输对象基类
 * @Date 2025/7/31
 * @Author LoveYue
 */
@Schema(description = "数据传输基类")
@Data
public abstract class BaseDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -5438179418828652248L;

    @Schema(description = "实体Id", type = "Long")
    private Long id;

    @Schema(description = "创建时间", type = "Date", format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @Schema(description = "创建人", type = "String")
    private String createBy;

    @Schema(description = "最后修改时间", type = "Date", format = "yyyy-MM-dd HH:mm:ss")
    private Date lastModifiedTime;

    @Schema(description = "最后修改人", type = "String")
    private String lastModifiedBy;

    @Schema(description = "删除时间", type = "Date", format = "yyyy-MM-dd HH:mm:ss")
    private Date deleteTime;

    @Schema(description = "删除人", type = "String")
    private String deletedBy;

    @Schema(description = "数据实体状态", type = "EntityStatusEnum")
    private EntityStatusEnum status;

    @Schema(description = "是否被删除", type = "Boolean")
    private Boolean deleted;

}
