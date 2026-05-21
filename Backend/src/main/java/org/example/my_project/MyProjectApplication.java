package org.example.my_project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
 * ========== Spring Boot 启动类 ==========
 *
 * @SpringBootApplication 是一个组合注解，等于同时加了：
 *   @Configuration  →  告诉Spring这个类可以做配置
 *   @EnableAutoConfiguration  →  SpringBoot自动装配（根据你的依赖自动配置，比如有mysql驱动就自动配置数据源）
 *   @ComponentScan  →  自动扫描当前包及子包下所有 @Component/@Service/@Controller/@Mapper 并注册为Bean
 *
 * 什么叫"注册为Bean"？
 *   Spring 启动时会扫描所有带 @Service/@Controller/@Mapper/@Component 的类，
 *   然后 new 出它们的实例对象，放在一个"大池子"（IOC容器）里统一管理。
 *   你写的 private final TicketService ticketService; 就是从池子里把对象取出来赋值。
 */
@SpringBootApplication
public class MyProjectApplication {

    /*
     * main方法，Java程序的入口。
     * SpringApplication.run() 做了三件事：
     *   1. 启动内嵌的Tomcat服务器（默认端口8080）
     *   2. 扫描并初始化所有Bean（@Service、@Controller、@Mapper等）
     *   3. 让Tomcat开始监听HTTP请求
     */
    public static void main(String[] args) {
        SpringApplication.run(MyProjectApplication.class, args);
    }

}
