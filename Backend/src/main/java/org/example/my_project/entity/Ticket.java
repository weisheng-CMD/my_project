package org.example.my_project.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/*
 * ========== 工单实体类 ==========
 *
 * 这个类的职责：和数据库的 ticket 表做"映射"。
 * 类的一个字段 = 表的一个列
 * 类的一个对象 = 表的一行数据
 *
 * 执行的SQL:  SELECT id, title, status FROM ticket WHERE id=1
 * MyBatis-Plus自动做的事：
 *   数据库列名 creator_id  ←→  自动转驼峰  ←→  Java字段 creatorId
 */

@Data       // 生成 getter/setter/toString：ticket.getTitle()
@Builder    // 构建者模式，让你这样创建对象：
            //   Ticket.builder().title("xxx").status("待处理").build()
@NoArgsConstructor   // 无参构造器：new Ticket()
@AllArgsConstructor  // 全参构造器：new Ticket(id, title, ...)
@TableName("ticket") // 告诉MyBatis-Plus：这个类对应数据库的 ticket 表
public class Ticket {

    /*
     * @TableId：标记这是主键字段
     * IdType.AUTO：主键值是数据库自动增长的（自增ID）
     * 执行 INSERT 时不传id，数据库自动给它一个值
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;        // 工单标题，对应数据库列 title

    private String description;  // 工单描述，对应数据库列 description

    private String status;       // 状态：待处理 / 处理中 / 已解决 / 已关闭
                                 // 对应数据库列 status

    private String priority;     // 优先级：低 / 中 / 高 / 紧急
                                 // 对应数据库列 priority

    private String category;     // 分类，对应数据库列 category

    private Long creatorId;      // 创建人ID，对应数据库列 creator_id
                                 // 驼峰 creatorId → 下划线 creator_id（自动转换）

    private Long assigneeId;     // 处理人ID，对应数据库列 assignee_id

    private LocalDateTime createTime;  // 创建时间，对应数据库列 create_time

    private LocalDateTime updateTime;  // 更新时间，对应数据库列 update_time

    /*
     * @TableLogic：逻辑删除标记
     * 当你调 deleteById() 时，MyBatis-Plus 不会真的 DELETE 一行，
     * 而是执行 UPDATE ticket SET deleted=1 WHERE id=?
     * 之后所有查询自动加 WHERE deleted=0
     */
//    @TableLogic
    @Builder.Default
    private Integer deleted = 0;     // @Builder.Default 让 Builder 不跳过默认值
}
