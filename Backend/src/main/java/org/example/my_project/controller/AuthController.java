package org.example.my_project.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.my_project.common.Result;
import org.example.my_project.entity.User;
import org.example.my_project.mapper.UserMapper;
import org.example.my_project.model.LoginRequest;
import org.example.my_project.model.LoginResponse;
import org.example.my_project.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * 认证接口：登录
 *
 * /login 在 SecurityConfig 里已经设为 permitAll()，不需要 Token 就能访问。
 */
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * POST /login
     *
     * 流程：
     *   1. @Valid 校验参数
     *   2. AuthenticationManager 拿用户名去数据库查，比对密码
     *   3. 认证成功 → 生成 JWT 返回
     *   4. 认证失败 → 抛异常，全局异常处理器统一返回错误
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {

        // Spring Security 认证：比对用户名和密码
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        // 认证成功，从数据库拿完整用户信息
        User user = userMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<User>()
                        .eq(User::getUsername, request.getUsername()));

        // 生成 JWT
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());

        LoginResponse resp = new LoginResponse(token, user.getId(), user.getUsername(), user.getRole());
        return Result.success("登录成功", resp);
    }

    /**
     * POST /register  （注册接口，仅开发测试用）
     */
    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody LoginRequest request) {
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword())) // BCrypt 加密
                .role("user")  // 默认注册为普通用户
                .build();
        userMapper.insert(user);
        return Result.success("注册成功", null);
    }
}
