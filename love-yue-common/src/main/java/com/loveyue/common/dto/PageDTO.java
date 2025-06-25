package com.loveyue.common.dto;

import com.loveyue.common.enums.SortDirection;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Description: 分页查询数据传输对象
 * @Date 2025/6/25
 * @Author LoveYue
 */
@Data
public class PageDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 4783954388680412204L;

    private static final Integer DEFAULT_PAGE_NO = 1;
    private static final Integer DEFAULT_PAGE_SIZE = 10;

    @Schema(description = "是否分页", type = "Boolean", defaultValue = "false")
    private Boolean paged = false;

    @Schema(description = "页号", type = "int", defaultValue = "1")
    private int pageNo;

    @Schema(description = "分页大小", type = "int", defaultValue = "10")
    private int pageSize;

    @Schema(description = "排序方式", type = "SortDirection", defaultValue = "ASC")
    private SortDirection direction = SortDirection.ASC;

    @Schema(description = "排序字段", type = "String")
    private String field;

    @Schema(description = "查询结果总数", type = "int")
    private int count;

    public PageDTO() {
        this.pageNo = DEFAULT_PAGE_NO;
        this.pageSize = DEFAULT_PAGE_SIZE;
    }

    public PageDTO(Integer pageNo, Integer pageSize) {
        this.pageNo = pageNo == null ? DEFAULT_PAGE_NO : pageNo;
        this.pageSize = pageSize == null ? DEFAULT_PAGE_SIZE : pageSize;
    }

    public PageDTO(int pageNo, int pageSize) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }

    private static Sort toSort(String order, String field) {
        if (order == null || field == null) {
            return Sort.by(new Sort.Order(Sort.Direction.DESC, "id"));
        }

        return Sort.by(new Sort.Order("descend".equals(order) ? Sort.Direction.DESC : Sort.Direction.ASC, field));
    }

    public Pageable toPageable() {
        if (Boolean.FALSE.equals(paged)) {
            return null;
        }

        return PageRequest.of(pageNo - 1, pageSize, toSort(direction.toString(), field));
    }
}
