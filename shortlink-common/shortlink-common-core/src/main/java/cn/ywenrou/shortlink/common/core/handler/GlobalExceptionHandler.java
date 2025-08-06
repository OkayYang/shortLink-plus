package cn.ywenrou.shortlink.common.core.handler;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.ywenrou.shortlink.common.core.domain.AjaxResult;
import cn.ywenrou.shortlink.common.core.enums.ErrorCodes;
import cn.ywenrou.shortlink.common.core.exception.base.BaseException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;
import java.util.Optional;

/**
 * 全局异常处理器
 * @author xuxiaoyang
 * @since 2025/6/25
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 拦截参数验证异常
     */
    @SneakyThrows
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public AjaxResult validExceptionHandler(HttpServletRequest request, MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        FieldError firstFieldError = CollectionUtil.getFirst(bindingResult.getFieldErrors());
        String exceptionStr = Optional.ofNullable(firstFieldError)
                .map(FieldError::getDefaultMessage)
                .orElse(StrUtil.EMPTY);
        log.error("[{}] {} [ex] {}", request.getMethod(), getUrl(request), exceptionStr);
        return AjaxResult.error(ErrorCodes.CLIENT_ERROR.code(), exceptionStr);
    }

    /**
     * 拦截应用内抛出的异常
     */
    @ExceptionHandler(value = {BaseException.class})
    public AjaxResult abstractException(HttpServletRequest request, BaseException ex) {
        if (ex.getCause() != null) {
            log.error("[{}] {} [ex] {}", request.getMethod(), request.getRequestURL().toString(), ex.toString(), ex.getCause());
            return AjaxResult.error(ex.getErrorCode(), ex.getErrorMessage());
        }
        log.error("[{}] {} [ex] {}", request.getMethod(), request.getRequestURL().toString(), ex.toString());
        return AjaxResult.error(ex.getErrorCode(), ex.getErrorMessage());
    }

    /**
     * 拦截未捕获异常
     */
    @ExceptionHandler(value = Throwable.class)
    public AjaxResult defaultErrorHandler(HttpServletRequest request, Throwable throwable) {
        log.error("[{}] {} ", request.getMethod(), getUrl(request), throwable);
        // 注意，此处是为了聚合模式添加的代码，正常不需要该判断
        if (Objects.equals(throwable.getClass().getSuperclass().getSimpleName(), BaseException.class.getSimpleName())) {
            String errorCode = ReflectUtil.getFieldValue(throwable, "errorCode").toString();
            String errorMessage = ReflectUtil.getFieldValue(throwable, "errorMessage").toString();
            return AjaxResult.error(errorCode, errorMessage);
        }
        return AjaxResult.error();
    }

    private String getUrl(HttpServletRequest request) {
        if (StringUtils.isEmpty(request.getQueryString())) {
            return request.getRequestURL().toString();
        }
        return request.getRequestURL().toString() + "?" + request.getQueryString();
    }
}
