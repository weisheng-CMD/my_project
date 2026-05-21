package org.example.my_project.manual;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/*
 * ========== 迷你 BaseMapper —— 把 MyBatis-Plus 的黑盒拆开给你看 ==========
 *
 * 这个类用 150 行代码，干了 BaseMapper 两件最核心的事：
 *   insert(entity)        → 反射读字段 → 拼 INSERT INTO → JdbcTemplate 执行
 *   selectById(Class, id) → 反射读类名 → 拼 SELECT * FROM → JdbcTemplate 执行
 *
 * 对比：
 *   MyBatis-Plus BaseMapper：接口 + 动态代理，内部 3000 行代码拼 SQL
 *   这个 MiniBaseMapper：   普通类 + 反射，   150 行代码拼 SQL
 *   结果：拼出来的 SQL 一模一样。
 *
 * 面试话术：
 *   "BaseMapper 底层就是反射读实体类的注解和字段，
 *    根据方法名（insert / selectById）匹配对应的 SQL 模板，
 *    拼接参数后通过 JDBC 发给数据库执行。"
 */
public class MiniBaseMapper<T> {

    /*
     * JdbcTemplate 是 Spring 对 JDBC 的封装。
     *
     * 原生 JDBC 写法（要 8 行）：
     *   Connection conn = dataSource.getConnection();
     *   PreparedStatement ps = conn.prepareStatement(sql);
     *   ps.setString(1, value);
     *   ps.executeUpdate();
     *   conn.close();
     *
     * JdbcTemplate 写法（1 行）：
     *   jdbcTemplate.update(sql, value);
     *
     * 底层还是 JDBC，只不过帮你管了连接、关闭、异常处理。
     */
    private final JdbcTemplate jdbcTemplate;

    public MiniBaseMapper(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // ==================== 插入 ====================

    /*
     * 你传入 ticket 对象（title="打印机坏了", status="待处理" ...）
     * 这个方法拼出 SQL 然后执行。
     */
    public int insert(T entity) {

        /*
         * entity.getClass() 拿到的不是数据，是 Ticket 类的"结构说明书"。
         *
         * 比如 entity 是 new Ticket()，但 entity.getClass() 返回的是 Ticket.class，
         * 这个 Class 对象描述了：
         *   - 类名：Ticket
         *   - 注解：@TableName("ticket")
         *   - 字段列表：[id, title, description, status, priority, ...]
         *   - 每个字段的注解：@TableId, @TableLogic 等
         */
        Class<?> clazz = entity.getClass();

        // 1. 从 @TableName 注解拿到表名 "ticket"
        String tableName = getTableName(clazz);

        // 2. 两个列表，一一对应：
        //    columns = [title, description, status, priority, category, creator_id, deleted]
        //    values  = ["打印机坏了", "三楼不出纸", "待处理", "高", "硬件", 1, 0]
        List<String> columns = new ArrayList<>();
        List<Object> values = new ArrayList<>();

        /*
         * clazz.getDeclaredFields() 返回 Ticket 类自己声明的所有字段（不包括父类的）：
         *   [id, title, description, status, priority, category, creatorId, assigneeId,
         *    createTime, updateTime, deleted]
         *
         * for (Field field : 字段数组)  →  一个一个处理每个字段
         */
        for (Field field : clazz.getDeclaredFields()) {

            /*
             * field.setAccessible(true) 是什么？
             *
             * Ticket.java 里字段是 private 的：private String title;
             * private 的意思是"外面不能碰"。你直接调 field.get(entity) 会报错。
             *
             * setAccessible(true) 就是绕过这个限制，暴力破解 private。
             * 翻译成人话："我知道它是 private，别拦我，我就要读。"
             *
             * 这是 Java 反射的特性：可以强读写 private 字段。
             */
            field.setAccessible(true);

            // ---- 跳过自增主键 ----
            /*
             * 如果字段上有 @TableId(type = IdType.AUTO)，说明这个列的值是数据库自动生成的。
             * INSERT 时不能传值（传了反而可能报错），所以跳过。
             *
             * 例如 Ticket.id：数据库自增，INSERT 不写 id 列，数据库自动填。
             */
            TableId tableId = field.getAnnotation(TableId.class);
            if (tableId != null && tableId.type() == IdType.AUTO) {
                continue;  // 跳过，不进 columns
            }

            try {
                /*
                 * field.get(entity) 就是用反射读出这个字段的当前值。
                 *
                 * 等价于：entity.getTitle()  →  "打印机坏了"
                 *        entity.getStatus()  →  "待处理"
                 *
                 * 但反射不需要知道字段名，for 循环自动处理所有字段。
                 */
                Object value = field.get(entity);

                // null 值的字段不插入，让数据库用默认值
                if (value == null) {
                    continue;
                }

                /*
                 * field.getName() 拿字段名（Java 驼峰），然后转成数据库下划线列名：
                 *   creatorId → creator_id
                 *   createTime → create_time
                 */
                columns.add(camelToUnderscore(field.getName()));
                values.add(value);

            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        /*
         * 3. 拼 SQL 字符串：
         *
         * columns     = [title, description, status, priority, category, creator_id, deleted]
         * columnStr   = "title, description, status, priority, category, creator_id, deleted"
         * placeholders = "?, ?, ?, ?, ?, ?, ?"
         *
         * 最终 SQL：
         *   INSERT INTO ticket (title, description, status, priority, category, creator_id, deleted)
         *   VALUES (?, ?, ?, ?, ?, ?, ?)
         *
         * 每个 ? 是一个占位符，values 数组里的值按顺序填入。
         * 用 ? 而不是直接拼字符串的原因是防止 SQL 注入。
         */
        String columnStr = String.join(", ", columns);
        String placeholders = String.join(", ", columns.stream().map(c -> "?").toList());
        String sql = "INSERT INTO " + tableName + " (" + columnStr + ") VALUES (" + placeholders + ")";

        /*
         * 4. JdbcTemplate 执行 SQL + 参数，底层等价于：
         *
         *   Connection conn = dataSource.getConnection();
         *   PreparedStatement ps = conn.prepareStatement(sql);
         *   ps.setString(1, "打印机坏了");      // 第1个 ? → title
         *   ps.setString(2, "三楼不出纸");      // 第2个 ? → description
         *   ps.setString(3, "待处理");          // 第3个 ? → status
         *   ... 依次设置所有 ?
         *   int rows = ps.executeUpdate();     // 执行，返回受影响行数（通常是1）
         *   conn.close();
         */
        return jdbcTemplate.update(sql, values.toArray());
    }

    // ==================== 根据主键查询 ====================

    /*
     * 传入 Ticket.class 和 id=5，查询并返回一个 Ticket 对象。
     */
    public T selectById(Class<T> clazz, Serializable id) {

        String tableName = getTableName(clazz);

        // 找到主键的列名（id 字段 → "id"）
        String idColumn = "id";  // 默认叫 id
        for (Field field : clazz.getDeclaredFields()) {
            if (field.getAnnotation(TableId.class) != null) {
                idColumn = camelToUnderscore(field.getName());
                break;
            }
        }

        // 拼 SQL：SELECT * FROM ticket WHERE id = ?
        String sql = "SELECT * FROM " + tableName + " WHERE " + idColumn + " = ?";

        /*
         * RowMapper 是把你从 ResultSet（数据库返回的原始数据）映射成 Ticket 对象的"翻译器"。
         *
         * 数据库返回的是一行行的列值：
         *   id=5, title="打印机坏了", status="待处理", ...
         *
         * RowMapper 的 mapRow 方法负责：
         *   1. new 一个空的 Ticket 对象
         *   2. 把每列的值塞进对应的字段：ticket.setTitle("打印机坏了")
         *   3. 返回组装好的 Ticket 对象
         *
         * MyBatis-Plus 的 ResultSet → Entity 映射比这个复杂几十倍，
         * 但核心逻辑一样：列名匹配字段名 → 反射设值。
         */
        RowMapper<T> rowMapper = (rs, rowNum) -> {
            try {
                // new 一个空的 Ticket 对象：new Ticket()
                T obj = clazz.getDeclaredConstructor().newInstance();

                // 遍历 Ticket 的每个字段
                for (Field field : clazz.getDeclaredFields()) {
                    field.setAccessible(true);  // 破解 private

                    // 把字段名转成列名：creatorId → creator_id
                    String colName = camelToUnderscore(field.getName());

                    // 从 ResultSet 里取对应列的值
                    // rs.getObject("title") → "打印机坏了"
                    Object value = rs.getObject(colName);

                    // 把值塞进 Ticket 对象的对应字段
                    // 等价于：ticket.setTitle("打印机坏了")
                    if (value != null) {
                        field.set(obj, value);
                    }
                }
                return obj;  // 返回组装好的 Ticket 对象

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };

        // 执行查询 + RowMapper 转换
        List<T> result = jdbcTemplate.query(sql, rowMapper, id);

        // 有结果就返回第一条，没结果返回 null
        return result.isEmpty() ? null : result.get(0);
    }

    // ==================== 工具方法 ====================

    /*
     * 从 @TableName 注解读表名。
     * 如果没写 @TableName，就用类名转下划线。
     *
     * 例如：@TableName("ticket") → "ticket"
     *       没写注解 → 类名 Ticket → "ticket"
     */
    private String getTableName(Class<?> clazz) {
        TableName annotation = clazz.getAnnotation(TableName.class);
        if (annotation != null) {
            return annotation.value();
        }
        return camelToUnderscore(clazz.getSimpleName());
    }

    /*
     * 驼峰转下划线。
     *
     * 遍历每个字符：
     *   小写字母 → 直接拼上
     *   大写字母 → 前面加 _，自己变小写
     *
     * "creatorId" → c r e a t o r → 遇到大写 I → 加 '_' + 'i' → "creator_id"
     * "createTime" → c r e a t e → 遇到大写 T → 加 '_' + 't' → "create_time"
     */
    private String camelToUnderscore(String camel) {
        StringBuilder sb = new StringBuilder();
        for (char c : camel.toCharArray()) {
            if (Character.isUpperCase(c)) {
                sb.append('_');                      // 加下划线
                sb.append(Character.toLowerCase(c)); // 变大写为小写
            } else {
                sb.append(c);                        // 小写直接拼上
            }
        }
        return sb.toString();
    }
}
