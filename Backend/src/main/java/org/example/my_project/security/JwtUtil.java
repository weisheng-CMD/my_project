package org.example.my_project.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 工具类：生成 Token、解析 Token、校验 Token
 *
 * 面试会问：JWT 由哪三部分组成？
 *   Header（算法类型）+ Payload（用户数据）+ Signature（签名防篡改）
 *   三部分用 Base64 编码，点号连接：xxxxx.yyyyy.zzzzz
 *
 *   JWT 不是加密的！Payload 只是 Base64 编码，任何人都能解码看到内容。
 *   所以绝对不能把密码放进 JWT。
 */
@Component
public class JwtUtil {

    private final SecretKey key;
    private final long expiration;

    public JwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long expiration) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiration = expiration;
    }

    /**
     * 生成 JWT Token
     * 把 userId 和 role 放进 Payload，过期时间由配置文件控制
     */
    public String generateToken(Long userId, String username, String role) {
        return Jwts.builder()
                .subject(username)                      // 用户名
                .claim("userId", userId)                // 自定义字段：用户ID
                .claim("role", role)                    // 自定义字段：角色
                .issuedAt(new Date())                   // 签发时间
                .expiration(new Date(System.currentTimeMillis() + expiration)) // 过期时间
                .signWith(key)                          // 用密钥签名
                .compact();
    }

    /**
     * 从请求头 Authorization: Bearer xxx 中提取 Token 字符串
     */
    public String extractToken(String header) {
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

    /**
     * 解析 Token，拿到里面存的所有信息（Claims）
     */
    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 校验 Token 是否有效：解析成功且未过期就是有效
     */
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
