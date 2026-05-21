package org.example.my_project.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * JWT 认证过滤器：每个请求进来都会经过它
 *
 * 位置：Filter → DispatcherServlet → Interceptor → AOP → Controller
 *
 * 做的事：
 *   1. 从请求头 Authorization 取 Token
 *   2. 解析 Token 拿到 userId、username、role
 *   3. 把用户信息放进 SecurityContext（Spring Security 全局上下文）
 *   4. 后续 @PreAuthorize 从 SecurityContext 读角色判断权限
 */
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        String token = jwtUtil.extractToken(request.getHeader("Authorization"));

        if (token != null && jwtUtil.validateToken(token)) {
            Claims claims = jwtUtil.parseToken(token);

            String username = claims.getSubject();
            String role = claims.get("role", String.class);
            Long userId = claims.get("userId", Long.class);

            // 把用户信息封装成 Spring Security 能识别的认证对象
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userId,           // principal：存 userId
                            null,             // credentials：无密码
                            List.of(new SimpleGrantedAuthority("ROLE_" + role))  // 权限
                    );

            // 放进 SecurityContext，后续 @PreAuthorize 就能读到了
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 不管有没有 Token，都让请求继续往下走
        // SecurityConfig 里配置了哪些接口必须登录，Spring Security 自己会拦
        chain.doFilter(request, response);
    }
}
