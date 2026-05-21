package org.example.my_project;

import lombok.RequiredArgsConstructor;
import org.example.my_project.entity.Ticket;
import org.example.my_project.entity.User;
import org.example.my_project.mapper.TicketMapper;
import org.example.my_project.mapper.UserMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 启动时初始化测试数据
 *
 * CommandLineRunner：Spring Boot 启动完成后自动执行 run 方法
 * 比 data.sql 灵活——能用 PasswordEncoder 加密密码
 */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserMapper userMapper;
    private final TicketMapper ticketMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        // 建测试用户（如果不存在）
        if (userMapper.selectById(1) == null) {
            userMapper.insert(User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("123456"))
                    .role("admin")
                    .build());
        }
        if (userMapper.selectById(2) == null) {
            userMapper.insert(User.builder()
                    .username("agent")
                    .password(passwordEncoder.encode("123456"))
                    .role("agent")
                    .build());
        }
        if (userMapper.selectById(3) == null) {
            userMapper.insert(User.builder()
                    .username("user")
                    .password(passwordEncoder.encode("123456"))
                    .role("user")
                    .build());
        }

        // 建测试工单（如果不存在）
        if (ticketMapper.selectById(1) == null) {
            ticketMapper.insert(Ticket.builder()
                    .title("无法登录系统")
                    .description("点击登录按钮后页面无响应")
                    .status("待处理")
                    .priority("高")
                    .category("账号问题")
                    .creatorId(3L)
                    .build());
        }
        if (ticketMapper.selectById(2) == null) {
            ticketMapper.insert(Ticket.builder()
                    .title("订单数据导出报错")
                    .description("导出数据时提示服务器内部错误")
                    .status("处理中")
                    .priority("紧急")
                    .category("数据问题")
                    .creatorId(3L)
                    .build());
        }

        System.out.println("========== 测试数据初始化完成 ==========");
        System.out.println("admin / 123456  (管理员)");
        System.out.println("agent / 123456  (客服)");
        System.out.println("user  / 123456  (普通用户)");
    }
}
