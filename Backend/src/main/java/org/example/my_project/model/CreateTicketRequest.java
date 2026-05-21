package org.example.my_project.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 创建工单的请求体
 *
 * @NotBlank: 不为 null、不为 ""、不为全空格
 *            校验失败时自动抛异常，由 GlobalExceptionHandler 统一处理
 */
@Data
public class CreateTicketRequest {

    @NotBlank(message = "工单标题不能为空")
    private String title;

    private String description;

    private String priority;

    @NotBlank(message = "工单分类不能为空")
    private String category;

    private Long creatorId;
}
