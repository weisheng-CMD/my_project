package org.example.my_project.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 *
 * @RestControllerAdvice = @ControllerAdvice + @ResponseBody
 *   拦截所有 Controller 抛出的异常，统一返回 Result 格式。
 *
 * 为什么需要？
 *   不用在每个 Controller 方法里写 try-catch，
 *   也不用写一堆 if null return error(404, "xxx")
 *   异常统一在这里处理，代码干净。
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ==================== 参数校验异常 ====================

    /**
     * @Valid 校验失败时触发（如 title 为空）
     * 把校验失败信息拼起来返回
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleValidation(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse("参数校验失败");
        return Result.error(400, msg);
    }

    // ==================== 认证异常 ====================

    /**
     * 用户名密码错误
     */
    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result<Void> handleBadCredentials(BadCredentialsException e) {
        return Result.error(401, "用户名或密码错误");
    }

    /**
     * 用户不存在
     */
    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result<Void> handleUserNotFound(UsernameNotFoundException e) {
        return Result.error(401, e.getMessage());
    }

    // ==================== 权限异常 ====================

    /**
     * 没权限访问（@PreAuthorize 拦截）
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Result<Void> handleAccessDenied(AccessDeniedException e) {
        return Result.error(403, "权限不足");
    }

    // ==================== 业务异常 ====================

    /**
     * 自定义业务异常：工单不存在(404)、参数错误(400)等
     */
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusiness(BusinessException e) {
        return Result.error(e.getCode(), e.getMessage());
    }

    // ==================== 兜底异常 ====================

    /**
     * 其他所有没预料到的异常，记日志并返回 500
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleUnknown(Exception e) {
        log.error("未预料的异常", e);
        return Result.error(500, "服务器内部错误: " + e.getMessage());
    }
}
