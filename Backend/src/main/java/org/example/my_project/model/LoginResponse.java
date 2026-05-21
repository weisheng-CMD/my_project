package org.example.my_project.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 登录成功返回的响应体：Token + 用户信息
 */
@Data
@AllArgsConstructor
public class LoginResponse {

    private String token;       // JWT Token，后续请求放 Authorization 头里
    private Long userId;
    private String username;
    private String role;
}
