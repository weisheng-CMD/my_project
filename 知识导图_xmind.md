# Spring Boot + MyBatis-Plus 知识导图

## 一、请求调用链
### POST /tickets 经过的每一站
#### DispatcherServlet
##### 收到HTTP请求
##### 读URL匹配Controller
#### TicketController.create()
##### @RequestBody把JSON转对象
##### 组装Ticket（Builder模式）
##### 调ticketService.save(ticket)
#### IService.save()
##### default方法在IService接口里
##### getBaseMapper().insert(entity)
#### TicketMapper代理对象
##### 拦截insert方法
##### 读@TableName→表名ticket
##### 反射读字段→列名列表
##### 驼峰转下划线→creator_id
##### 排除自增主键id
##### 拼INSERT INTO SQL
##### SqlSession→JDBC→MySQL
#### MySQL执行返回结果

## 二、反射三剑客
### Class<?> 类的说明书
#### 存了什么
##### 类名
##### 字段列表
##### 注解信息
#### getClass()
##### 从实例反推类
##### ticket.getClass()→Ticket.class
#### 为什么需要
##### 运行时知道表名
##### 运行时知道列名
##### 不用硬编码

### Field 字段的说明书
#### getDeclaredFields()
##### 拿全部字段数组
##### [title, status, priority...]
#### setAccessible(true)
##### 暴力破解private
##### 之后才能reflection读写
#### getName()
##### 拿Java字段名
##### creatorId→需转creator_id
#### get(entity)
##### 读字段当前值
##### 等价entity.getTitle()

### 注解 标签信息
#### @TableName
##### 类上
##### 指定映射的表名
#### @TableId
##### 字段上
##### IdType.AUTO=自增
##### INSERT跳过此列
#### @TableLogic
##### 字段上或YAML
##### 查询加WHERE deleted=0
##### 删除变UPDATE

## 三、动态代理
### 问题
#### Mapper是接口无实现类
#### 为什么insert能执行
### 答案：运行时代理
#### @Mapper接口空壳
#### 启动时Spring扫描
#### Proxy.newProxyInstance创建代理
#### MapperProxy.invoke拦截调用
#### 根据方法名匹配SQL模板
#### 反射读实体拼SQL
#### SqlSession执行
### 面试话术
#### 启动时为每个@Mapper生成JDK动态代理
#### 代理拦截方法调用
#### 匹配CRUD操作
#### 反射读注解拼SQL
#### 控制台打印生成的SQL

## 四、Service继承链
### IService接口
#### default save()
#### default getById()
#### default updateById()
#### default removeById()
### ServiceImpl抽象类
#### implements IService
#### 额外提供saveBatch
#### 额外提供分页方法
### TicketService
#### extends ServiceImpl
#### 继承全部CRUD方法
#### 只写自定义业务
##### listByStatus
##### assign

## 五、@Transactional事务
### 不加注解
#### SqlSession不注册同步
#### JDBC连接Spring不管
#### 每条SQL自动提交
#### 抛异常不回滚
### 加注解
#### SqlSession注册同步
#### JDBC连接Spring管理
#### setAutoCommit(false)
#### 成功commit
#### 异常rollback
### 底层
#### Spring AOP代理
#### 方法前开启事务
#### 方法后提交或回滚

## 六、踩过的坑
### 坑1 Builder忽略默认值
#### deleted=0
#### Builder.build()变null
#### 加@Builder.Default修复
### 坑2 逻辑删除双路生效
#### @TableLogic注解一条路
#### YAML全局配置另一条路
#### 任意一条通了就生效
#### 两条都关才变物理删除
### 坑3 RestController vs Controller
#### RestController=Controller+ResponseBody
#### Controller缺ResponseBody
#### 返回值当视图文件名
#### 找不到模板就报错
### 坑4 NULL+逻辑删除=死局
#### YAML配了logic-delete-field
#### 查询加WHERE deleted=0
#### Builder导致deleted=NULL
#### NULL!=0永远false
#### 永远查不到

## 七、SQL注入与占位符
### #{}
#### PreparedStatement占位符
#### 值自动转义防注入
#### 用户输入必须用
### ${}
#### 字符串直接拼接
#### 无转义可被注入
#### 只用于表名列名

## 八、还缺的知识模块
### JDBC底层
#### Connection
#### PreparedStatement
#### ResultSet
### IOC容器原理
#### ConcurrentHashMap存储
#### Bean生命周期
#### 构造器注入vs字段注入
### MyBatis XML手写
#### resultMap映射
#### 动态SQL
#### 多表关联
### 分页
#### Page插件
#### 物理分页vs内存分页
### 全局异常处理
#### @ControllerAdvice
#### 统一返回格式
### 参数校验
#### @Valid
#### @NotBlank
### MySQL索引
#### EXPLAIN执行计划
#### 覆盖索引
#### 最左前缀
