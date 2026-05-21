package org.example.my_project.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 登录请求体
 *
 * @NotBlank: 不能为 null、不能是空字符串、不能全是空格
 */
@Data
public class LoginRequest {

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;
}
