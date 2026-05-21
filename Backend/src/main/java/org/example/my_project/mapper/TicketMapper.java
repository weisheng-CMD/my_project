package org.example.my_project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.my_project.entity.Ticket;

/*
 * ========== 工单 Mapper（数据访问层） ==========
 *
 * 这个接口只做一件事：和数据库的 ticket 表交互。
 *
 * 为什么是接口而不是类？
 *   你写的是接口，MyBatis-Plus 在运行时用"动态代理"生成一个实现类。
 *   这个实现类会自动把 Ticket 上的 @TableName、@TableId 等注解
 *   翻译成真实的 SQL 去执行。
 *
 * BaseMapper<Ticket> 里自带的方法（你不用写）：
 *   insert(Ticket)        → INSERT INTO ticket (...)
 *   deleteById(Long)      → UPDATE ticket SET deleted=1 WHERE id=?   // 因为@TableLogic，走逻辑删除
 *   updateById(Ticket)    → UPDATE ticket SET ... WHERE id=?
 *   selectById(Long)      → SELECT * FROM ticket WHERE id=? AND deleted=0
 *   selectList(条件)       → SELECT * FROM ticket WHERE deleted=0 AND ...
 *
 * @Mapper 注解：
 *   告诉Spring："启动时给这个接口生成代理实现类，注册成Bean"
 *   这样 Service 里 private final TicketMapper ticketMapper; 才能注入。
 */
@Mapper
public interface TicketMapper extends BaseMapper<Ticket> {
    // 空的！常规增删改查 BaseMapper 都帮你写好了。
    // 将来复杂查询（如多表关联）可以在这里加自定义方法。
}
