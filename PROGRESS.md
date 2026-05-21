# 进度交接文档

## 目标

一个月后投递小厂/外包 Java 初级开发岗位。

---

## 当前项目

工单管理系统（Spring Boot 3.3.7 + MyBatis-Plus 3.5.7 + MySQL）

源码位置：`Backend/`

---

## 已掌握

| 模块 | 程度 | 证据 |
|------|------|------|
| MVC 调用链 | 能讲清楚 | Debug 跟踪了 DispatcherServlet → Controller → IService → Mapper 代理 → JDBC |
| BaseMapper 动态代理 | 理解原理 | 自己写了 MiniBaseMapper（反射 + JdbcTemplate 拼 SQL） |
| @Transactional | 理解 | 做了有/无事务对比实验，看到 SqlSession 日志区别 |
| 逻辑删除 | 理解 + 踩过坑 | 知道 YAML 全局配置和 @TableLogic 各自独立生效 |
| Lombok @Builder.Default | 踩过坑 | Builder 跳过字段默认值，deleted=NULL 导致查不到 |
| MySQL 索引最左前缀 | 方向对 | 能说清楚什么时候走索引什么时候不走 |
| 覆盖索引 vs 回表 | OK | 能区分 |

## 自测分数

Java 基础 5 题：59%（线程池流程记错，HashMap 扩容倍数、头尾插反）
MySQL 2 题：72%

---

## 还没加到项目里的（第1周任务）

- [ ] Spring Security + JWT 登录鉴权
- [ ] 全局异常处理（@ControllerAdvice）
- [ ] 参数校验（@Valid / @NotBlank）
- [ ] 项目 README（项目描述、启动方式、接口文档）

## 还没学的

- [ ] IOC 容器原理（Bean 生命周期、循环依赖）
- [ ] Filter / Interceptor / AOP 完整链路
- [ ] Java 基础突击（HashMap 细节补正、线程池、synchronized、JVM）
- [ ] MySQL EXPLAIN 执行计划
- [ ] Redis 基础（5 种数据类型、缓存穿透/击穿/雪崩）

## 知识导图文件

- `知识导图.md` — VS Code 预览用，完整版含表格代码块
- `知识导图_xmind.md` — XMind 导入用，纯树形短标题

## 关键教训

1. YAML 配置和注解可能各自独立生效（逻辑删除是双路）
2. Lombok @Builder 会跳过字段默认值，必须加 @Builder.Default
3. @RestController = @Controller + @ResponseBody
4. IService 里的 save/getById 是 default 方法，不是 ServiceImpl 里的
5. HashMap 扩容是 2 倍不是 1.5 倍（1.5 倍是 ArrayList）
6. 线程池和虚拟线程是两条平行线，不能混
7. 面试不要背答案，要能精确答——细节错了就崩

## 下一步

休息复习导图 → 然后加 Security + JWT 功能到项目
