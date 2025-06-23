package entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonSetter;
import enums.EntityStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.util.Date;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

/**
 * @Description: 业务数据实体基类
 * @Date 2025/6/23
 * @Author LoveYue
 */
@Getter
public abstract class BaseBusinessEntity extends BaseEntity {
    @Serial
    private static final long serialVersionUID = -6530988839804601100L;

    @Schema(description = "创建时间", type = "Date", format = "yyyy-MM-dd HH:mm:ss", requiredMode = REQUIRED)
    @Column(nullable = false)
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8"
    )
    private Date createTime;

    @Schema(description = "创建人", type = "String", requiredMode = REQUIRED)
    @Column(nullable = false)
    @Setter
    private String createBy;

    @Schema(description = "最后修改时间", type = "Date", format = "yyyy-MM-dd HH:mm:ss", requiredMode = REQUIRED)
    @Column(nullable = false)
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8"
    )
    private Date lastModifiedTime;

    @Schema(description = "最后修改人", type = "String", requiredMode = REQUIRED)
    @Column(nullable = false)
    @Setter
    private String lastModifiedBy;

    @Schema(description = "删除时间", type = "Date", format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8"
    )
    @Setter
    private Date deleteTime;

    @Schema(description = "删除人", type = "String")
    @Column
    @Setter
    private String deletedBy;

    @Schema(description = "数据实体状态", type = "EntityStatusEnum", requiredMode = REQUIRED)
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EntityStatusEnum status;

    @Schema(description = "是否被删除", type = "Boolean")
    @Column
    @Setter
    private Boolean deleted;

    protected BaseBusinessEntity() {
        this(null);
        this.setCreateTime();
        this.setLastModifiedTime();
        this.setStatus();
    }

    protected BaseBusinessEntity(Long id) {
        super(id);
        this.setCreateTime();
        this.setLastModifiedTime();
        this.setCreateTime();
    }

    @JsonSetter
    public void setCreateTime() {
        this.createTime = new Date();
    }

    @JsonSetter
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @JsonSetter
    public void setLastModifiedTime() {
        this.lastModifiedTime = new Date();
    }

    @JsonSetter
    public void setLastModifiedTime(Date lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    public void setStatus(EntityStatusEnum status) {
        this.status = status;
    }

    public void setStatus() {
        this.status = EntityStatusEnum.EFFECTIVE;
    }
}
