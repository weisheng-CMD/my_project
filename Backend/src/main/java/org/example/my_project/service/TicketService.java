package org.example.my_project.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.example.my_project.entity.Ticket;
import org.example.my_project.mapper.TicketMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/*
 * ========== 工单 Service（业务逻辑层） ==========
 *
 * Controller 只负责收请求和返回结果，具体"做什么"都交给 Service。
 *
 * ServiceImpl<TicketMapper, Ticket> 是什么？
 *   继承它等于自动拥有以下方法（不用你写！）：
 *     save(Ticket)          → 插入一条工单
 *     getById(Long)         → 根据id查一条工单
 *     updateById(Ticket)    → 根据id更新一条工单
 *     removeById(Long)      → 根据id逻辑删除一条工单
 *   这些方法在 ServiceImpl 里已经有具体实现，实现就是调 TicketMapper 的对应方法。
 *
 * LambdaQueryWrapper 是什么？
 *   MyBatis-Plus提供的条件构造器，让你用Java代码写查询条件，不用手写SQL。
 *   例如：wrapper.eq(Ticket::getStatus, "待处理")  →  WHERE status = '待处理'
 *        wrapper.orderByDesc(Ticket::getCreateTime) → ORDER BY create_time DESC
 *
 * @Service：
 *   告诉Spring："把这个类注册成Bean"
 *   这样 Controller 里 private final TicketService ticketService; 才能注入。
 */
@Service
@RequiredArgsConstructor  // 给 final 字段生成构造器，用于注入 TicketMapper
public class TicketService extends ServiceImpl<TicketMapper, Ticket> {

    private final TicketMapper ticketMapper;  // Spring会自动注入Mapper的代理对象

    /*
     * 按状态筛选工单列表
     *
     * 如果传了status，只查该状态的：
     *   ticketMapper.selectList(wrapper)  →  SELECT * FROM ticket WHERE status=? AND deleted=0
     * 如果没传status，查全部：
     *   ticketMapper.selectList(new LambdaQueryWrapper<>())  →  SELECT * FROM ticket WHERE deleted=0
     */
    public List<Ticket> listByStatus(String status) {
        LambdaQueryWrapper<Ticket> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(status)) {
            wrapper.eq(Ticket::getStatus, status);  // WHERE status = ?
        }
        wrapper.orderByDesc(Ticket::getCreateTime);  // ORDER BY create_time DESC
        return ticketMapper.selectList(wrapper);      // 执行查询，返回List<Ticket>
    }

    /*
     * 更新工单状态
     *
     * updateById 只需要传入有id和非空字段的对象，
     * MyBatis-Plus 只更新非null的字段：
     *   UPDATE ticket SET status=? WHERE id=?
     */
    public boolean updateStatus(Long id, String status) {
        Ticket ticket = new Ticket();
        ticket.setId(id);
        ticket.setStatus(status);
        return updateById(ticket);  // 继承自ServiceImpl的方法
    }

    /*
     * 分配工单给处理人
     *
     * 同上，只更新 assignee_id 字段：
     *   UPDATE ticket SET assignee_id=? WHERE id=?
     */
    public boolean assign(Long id, Long assigneeId) {
        Ticket ticket = new Ticket();
        ticket.setId(id);
        ticket.setAssigneeId(assigneeId);
        return updateById(ticket);  // 继承自ServiceImpl的方法
    }

}
