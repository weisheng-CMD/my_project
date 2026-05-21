package org.example.my_project.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 配置类
 *
 * 核心策略：
 *   1. 关闭 Session（JWT 无状态，不依赖 Session）
 *   2. 放开 /login 让任何人能登录
 *   3. 其余接口必须带有效 JWT
 *   4. 开启方法级权限注解 @PreAuthorize
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)   // 开启 @PreAuthorize
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    /**
     * 密码加密器：BCrypt，不可逆加密
     * 同一个密码每次加密结果不同（自带随机盐），不能反推原文
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * AuthenticationManager：Spring Security 的认证管理器
     * 登录时用它验证用户名密码
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * 安全过滤链：定义哪些接口保护、哪些放开
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 关闭 CSRF（前后端分离 + JWT 不需要）
            .csrf(csrf -> csrf.disable())

            // 无状态 Session（JWT 自带信息，不存 Session）
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // 接口权限规则
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/register").permitAll()  // 登录、注册放开
                .requestMatchers(HttpMethod.GET, "/tickets/**").hasAnyRole("user", "agent", "admin") // 查看：所有角色
                .requestMatchers(HttpMethod.POST, "/tickets/**").hasAnyRole("user", "agent", "admin") // 创建：用户、客服和管理员都能建工单
                .requestMatchers(HttpMethod.PUT, "/tickets/**").hasAnyRole("agent", "admin")         // 处理：客服和管理员
                .requestMatchers(HttpMethod.DELETE, "/tickets/**").hasRole("admin")                 // 删除：仅管理员
                .anyRequest().authenticated()
            )

            // 把 JWT 过滤器插在 UsernamePasswordAuthenticationFilter 之前
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
