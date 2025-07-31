package com.loveyue.common.handler;

import com.loveyue.common.dto.BaseDTO;
import com.loveyue.common.enums.ResponseCode;
import com.loveyue.common.exception.BusinessException;
import com.loveyue.common.response.ObjectResponse;
import com.loveyue.common.utils.ResponseUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.Collectors;

/**
 * @Description: 全局异常处理器
 * @Date 2025/7/31
 * @Author LoveYue
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理业务异常，将异常信息封装后返回给客户端。
     *
     * @param e       业务异常对象
     * @param request HTTP请求对象
     * @return 包含错误信息的响应实体
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ObjectResponse<BaseDTO>> handleBusinessException(BusinessException e, HttpServletRequest request) {
        logger.warn("业务异常：{} - {}", e.getCode(), e.getMessage());

        ObjectResponse<BaseDTO> response = ResponseUtils.error(e.getCode(), e.getMessage());
        return ResponseEntity.ok(response);
    }

    /**
     * 处理方法参数校验失败异常，将异常信息封装后返回给客户端。
     *
     * @param e       方法参数校验失败异常对象
     * @param request HTTP请求对象
     * @return 包含错误信息的响应实体
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ObjectResponse<BaseDTO>> handleValidationException(MethodArgumentNotValidException e, HttpServletRequest request) {
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));

        logger.warn("参数校验失败: {}", errorMessage);

        ObjectResponse<BaseDTO> response = ResponseUtils.error(
                ResponseCode.VALIDATION_FAILED.getCode(),
                "参数校验失败: {}" + errorMessage
        );

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * 处理参数绑定异常，将异常信息封装后返回给客户端。
     *
     * @param e       参数绑定异常对象
     * @param request HTTP请求对象
     * @return 包含错误信息的响应实体
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ObjectResponse<BaseDTO>> handleBindException(BindException e, HttpServletRequest request) {
        String errorMessage = e.getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));

        logger.warn("参数绑定失败: {}", errorMessage);

        ObjectResponse<BaseDTO> response = ResponseUtils.error(
                ResponseCode.BAD_REQUEST.getCode(),
                "参数绑定失败: {}" + errorMessage
        );

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * 处理约束违反异常，将异常信息封装后返回给客户端。
     *
     * @param e       约束违反异常对象
     * @param request HTTP请求对象
     * @return 包含错误信息的响应实体
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ObjectResponse<BaseDTO>> handleConstraintViolationException(ConstraintViolationException e, HttpServletRequest request) {

        String errorMessage = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("; "));

        logger.warn("约束违反: {}", errorMessage);

        ObjectResponse<BaseDTO> response = ResponseUtils.error(
                ResponseCode.VALIDATION_FAILED.getCode(),
                "约束违反: " + errorMessage
        );

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * 处理方法参数类型不匹配异常，将异常信息封装后返回给客户端。
     *
     * @param e       方法参数类型不匹配异常对象
     * @param request HTTP请求对象
     * @return 包含错误信息的响应实体
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ObjectResponse<BaseDTO>> handleTypeMismatchException(MethodArgumentTypeMismatchException e, HttpServletRequest request) {

        logger.warn("参数类型不匹配: {} - {}", e.getName(), e.getValue());

        ObjectResponse<BaseDTO> response = ResponseUtils.error(
                ResponseCode.BAD_REQUEST.getCode(),
                String.format("参数 '%s' 的值 '%s' 类型不正确", e.getName(), e.getValue())
        );

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * 处理非法参数异常，将异常信息封装后返回给客户端。
     *
     * @param e       非法参数异常对象
     * @param request HTTP请求对象
     * @return 包含错误信息的响应实体
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ObjectResponse<BaseDTO>> handleIllegalArgumentException(IllegalArgumentException e, HttpServletRequest request) {

        logger.warn("非法参数异常: {}", e.getMessage());

        ObjectResponse<BaseDTO> response = ResponseUtils.error(
                ResponseCode.BAD_REQUEST.getCode(),
                "参数错误: " + e.getMessage()
        );

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * 处理未被其他异常处理器捕获的通用异常。
     *
     * @param e       异常对象
     * @param request HTTP请求对象
     * @return 包含错误信息的响应实体，状态码为500（INTERNAL_SERVER_ERROR）
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ObjectResponse<BaseDTO>> handleGenericException(Exception e, HttpServletRequest request) {

        logger.error("未处理的异常: {} - {}", e.getClass().getSimpleName(), e.getMessage(), e);

        ObjectResponse<BaseDTO> response = ResponseUtils.error(ResponseCode.INTERNAL_SERVER_ERROR);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
