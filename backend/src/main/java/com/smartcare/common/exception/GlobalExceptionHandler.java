package com.smartcare.common.exception;

import com.smartcare.common.Result;
import com.smartcare.common.ResultCodeEnum;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e, HttpServletRequest request) {
        log.warn("业务异常，请求路径: {}, 错误信息: {}", request.getRequestURI(), e.getMessage());
        return Result.build(null, e.getCode(), e.getMessage());
    }

    /**
     * @RequestBody 参数校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e,
                                                              HttpServletRequest request) {
        String message = ResultCodeEnum.PARAM_ERROR.getMessage();
        if (e.getBindingResult().getFieldError() != null) {
            message = e.getBindingResult().getFieldError().getDefaultMessage();
        }
        log.warn("参数校验异常，请求路径: {}, 错误信息: {}", request.getRequestURI(), message);
        return Result.build(null, ResultCodeEnum.PARAM_ERROR.getCode(), message);
    }

    /**
     * 表单绑定异常
     */
    @ExceptionHandler(BindException.class)
    public Result<Void> handleBindException(BindException e, HttpServletRequest request) {
        String message = ResultCodeEnum.PARAM_ERROR.getMessage();
        if (e.getBindingResult().getFieldError() != null) {
            message = e.getBindingResult().getFieldError().getDefaultMessage();
        }
        log.warn("参数绑定异常，请求路径: {}, 错误信息: {}", request.getRequestURI(), message);
        return Result.build(null, ResultCodeEnum.PARAM_ERROR.getCode(), message);
    }

    /**
     * 数据库唯一约束异常
     */
    @ExceptionHandler(DuplicateKeyException.class)
    public Result<Void> handleDuplicateKeyException(DuplicateKeyException e, HttpServletRequest request) {
        log.error("数据库唯一键冲突，请求路径: {}", request.getRequestURI(), e);
        return Result.build(null, ResultCodeEnum.DATA_ALREADY_EXIST.getCode(), "数据已存在");
    }

    /**
     * 非法参数异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public Result<Void> handleIllegalArgumentException(IllegalArgumentException e, HttpServletRequest request) {
        log.warn("非法参数异常，请求路径: {}, 错误信息: {}", request.getRequestURI(), e.getMessage());
        return Result.build(null, ResultCodeEnum.PARAM_ERROR.getCode(), e.getMessage());
    }

    /**
     * 兜底异常
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e, HttpServletRequest request) {
        log.error("系统异常，请求路径: {}", request.getRequestURI(), e);
        return Result.build(null, ResultCodeEnum.FAIL.getCode(), "系统繁忙，请稍后再试");
    }
}