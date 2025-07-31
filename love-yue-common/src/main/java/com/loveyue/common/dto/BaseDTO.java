package com.loveyue.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Description: 数据传输对象基类
 * @Date 2025/7/31
 * @Author LoveYue
 */
@Schema(description = "数据传输基类")
public abstract class BaseDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -5438179418828652248L;
}
