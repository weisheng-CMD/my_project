package org.example.my_project.model;

import lombok.Data;

/*
 * ========== 分配工单的请求体 ==========
 *
 * 前端 PUT /tickets/5/assign 时，请求体是JSON：
 *   { "assigneeId": 2 }
 *
 * 把工单分配给id=2的处理人。
 */
@Data
public class AssignRequest {

    private Long assigneeId;  // 处理人的用户ID
}
