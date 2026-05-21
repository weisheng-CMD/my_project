package org.example.my_project.common;

import lombok.Getter;

/**
 * 业务异常：工单不存在、权限不足等场景
 * 不要每个 Controller 方法写 if null return error，直接 throw 这个异常
 * GlobalExceptionHandler 统一处理
 */
@Getter
public class BusinessException extends RuntimeException {

    private final int code;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    /** 资源不存在：工单/用户查不到 */
    public static BusinessException notFound(String message) {
        return new BusinessException(404, message);
    }

    /** 业务逻辑错误 */
    public static BusinessException badRequest(String message) {
        return new BusinessException(400, message);
    }
}
