# 智能客服工单系统

基于 Spring Boot 3.x + MyBatis-Plus + JDK 21 构建的工单管理基础 CRUD 系统。

## 技术栈

| 技术 | 版本 |
|------|------|
| Java | 21 |
| Spring Boot | 3.3.7 |
| MyBatis-Plus | 3.5.7 |
| MySQL | 8.0+ |
| Lombok | 最新 |
| Maven | 3.6+ |

## 项目结构

```
src/main/java/org/example/my_project/
├── MyProjectApplication.java        # 启动类
├── common/
│   └── Result.java                  # 统一响应类
├── entity/
│   ├── Ticket.java                  # 工单实体
│   └── User.java                    # 用户实体
├── mapper/
│   ├── TicketMapper.java            # 工单 Mapper
│   └── UserMapper.java              # 用户 Mapper
├── model/
│   ├── CreateTicketRequest.java     # 创建工单 DTO
│   ├── UpdateStatusRequest.java     # 更新状态 DTO
│   └── AssignRequest.java           # 分配工单 DTO
├── service/
│   └── TicketService.java           # 工单 Service
└── controller/
    └── TicketController.java        # 工单 Controller

src/main/resources/
├── application.yml                  # 配置文件
├── sql/
│   └── schema.sql                   # 建表 + 测试数据
└── static/
    └── index.html                   # API 调试面板
```

## 快速开始

### 1. 创建数据库

```sql
CREATE DATABASE your_database_name DEFAULT CHARSET utf8mb4;
```

### 2. 执行建表脚本

执行 `src/main/resources/sql/schema.sql` 中的 SQL 语句。

### 3. 修改数据库配置

编辑 `src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/your_database_name?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
    username: your_username
    password: your_password
```

### 4. 启动项目

```bash
mvn spring-boot:run
```

### 5. 打开调试面板

浏览器访问：http://localhost:8080

## API 接口文档

所有接口统一返回格式：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": { }
}
```

### 1. 创建工单

```
POST /tickets
Content-Type: application/json

{
  "title": "工单标题",
  "description": "工单描述",
  "priority": "高",
  "category": "账号问题",
  "creatorId": 1
}
```

### 2. 查看工单详情

```
GET /tickets/{id}
```

### 3. 按状态筛选工单列表

```
GET /tickets?status=待处理
GET /tickets          （不传 status 则查询全部）
```

### 4. 更新工单状态

```
PUT /tickets/{id}/status
Content-Type: application/json

{ "status": "处理中" }
```

### 5. 分配工单

```
PUT /tickets/{id}/assign
Content-Type: application/json

{ "assigneeId": 2 }
```

### 6. 删除工单（逻辑删除）

```
DELETE /tickets/{id}
```

## 状态枚举

| 字段 | 可选值 |
|------|--------|
| 工单状态 | 待处理 / 处理中 / 已解决 / 已关闭 |
| 优先级 | 低 / 中 / 高 / 紧急 |
| 用户角色 | user / agent / admin |

## 逻辑删除说明

工单删除采用 MyBatis-Plus 逻辑删除，`deleted` 字段为 `1` 时表示已删除。查询和操作时会自动过滤已删除数据。
