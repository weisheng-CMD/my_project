package org.example.my_project.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.my_project.common.BusinessException;
import org.example.my_project.common.Result;
import org.example.my_project.entity.Ticket;
import org.example.my_project.manual.MiniBaseMapper;
import org.example.my_project.model.AssignRequest;
import org.example.my_project.model.CreateTicketRequest;
import org.example.my_project.model.UpdateStatusRequest;
import org.example.my_project.service.TicketService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.util.List;

/**
 * 工单控制器
 *
 * 权限在 SecurityConfig 里统一配置：
 *   GET    /tickets/** → user / agent / admin
 *   POST   /tickets/** → user / admin
 *   PUT    /tickets/** → agent / admin
 *   DELETE /tickets/** → admin
 */
@RestController
@RequestMapping("/tickets")
public class TicketController {

    private final TicketService ticketService;
    private final MiniBaseMapper<Ticket> miniBaseMapper;

    public TicketController(TicketService ticketService, DataSource dataSource) {
        this.ticketService = ticketService;
        this.miniBaseMapper = new MiniBaseMapper<>(new JdbcTemplate(dataSource));
    }

    // ==================== 创建工单 ====================

    @PostMapping
    public Result<Ticket> create(@Valid @RequestBody CreateTicketRequest request) {
        Ticket ticket = Ticket.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .priority(request.getPriority() != null ? request.getPriority() : "中")
                .category(request.getCategory())
                .creatorId(request.getCreatorId())
                .status("待处理")
                .build();
        ticketService.save(ticket);
        return Result.success("工单创建成功", ticket);
    }

    // ==================== 工单详情 ====================

    @GetMapping("/{id}")
    public Result<Ticket> getById(@PathVariable Long id) {
        Ticket ticket = ticketService.getById(id);
        if (ticket == null) throw BusinessException.notFound("工单不存在");
        return Result.success(ticket);
    }

    // ==================== 工单列表 ====================

    @GetMapping
    public Result<List<Ticket>> list(@RequestParam(required = false) String status) {
        return Result.success(ticketService.listByStatus(status));
    }

    // ==================== 更新状态 ====================

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id,
                                     @RequestBody UpdateStatusRequest request) {
        if (ticketService.getById(id) == null) throw BusinessException.notFound("工单不存在");
        ticketService.updateStatus(id, request.getStatus());
        return Result.success("状态更新成功", null);
    }

    // ==================== 分配工单 ====================

    @PutMapping("/{id}/assign")
    public Result<Void> assign(@PathVariable Long id,
                               @RequestBody AssignRequest request) {
        if (ticketService.getById(id) == null) throw BusinessException.notFound("工单不存在");
        ticketService.assign(id, request.getAssigneeId());
        return Result.success("分配成功", null);
    }

    // ==================== 删除工单 ====================

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        if (ticketService.getById(id) == null) throw BusinessException.notFound("工单不存在");
        ticketService.removeById(id);
        return Result.success("删除成功", null);
    }

    // ==================== MiniBaseMapper 实验接口 ====================

    @PostMapping("/mini")
    public Result<Ticket> createWithMini(@Valid @RequestBody CreateTicketRequest request) {
        Ticket ticket = Ticket.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .priority(request.getPriority() != null ? request.getPriority() : "中")
                .category(request.getCategory())
                .creatorId(request.getCreatorId())
                .status("待处理")
                .build();
        miniBaseMapper.insert(ticket);
        return Result.success("MiniBaseMapper 插入成功", ticket);
    }

    @GetMapping("/mini/{id}")
    public Result<Ticket> getByIdWithMini(@PathVariable Long id) {
        Ticket ticket = miniBaseMapper.selectById(Ticket.class, id);
        if (ticket == null) throw BusinessException.notFound("工单不存在");
        return Result.success(ticket);
    }
}
