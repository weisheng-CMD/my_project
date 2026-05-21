package org.example.my_project.model;

import lombok.Data;

/*
 * ========== 更新状态的请求体 ==========
 *
 * 前端 PUT /tickets/5/status 时，请求体是JSON：
 *   { "status": "处理中" }
 *
 * 只有一个字段，但因为用了 @RequestBody 接收JSON，还是得用一个类来承载。
 */
@Data
public class UpdateStatusRequest {

    private String status;  // 新状态
}
