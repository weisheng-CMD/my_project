package org.example.my_project.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * ========== 统一响应结果类 ==========
 *
 * 为什么需要这个类？
 *   前端每次请求后端，返回的JSON格式必须统一，不然前端不知道是成功还是失败。
 *   比如成功返回：{ "code": 200, "message": "操作成功", "data": {...} }
 *   失败返回：   { "code": 500, "message": "工单不存在", "data": null }
 *   所有接口都用这个 Result 包装，前端解析格式一致。
 *
 * <T> 是什么意思？
 *   泛型，代表 data 可以是任意类型。
 *   Result<Ticket> → data 是Ticket对象
 *   Result<List<Ticket>> → data 是Ticket列表
 *
 * 静态工厂方法：
 *   不用每次 new Result(200, "成功", data)，直接用 Result.success(data) 更简洁。
 */


@Data                   // Lombok自动生成 getter/setter/toString
@NoArgsConstructor      // 生成无参构造器
@AllArgsConstructor     // 生成全参构造器
public class Result<T> {

    private Integer code;    // 状态码：200=成功，500=服务器错误，404=未找到
    private String message;  // 提示信息：给前端看的文字
    private T data;          // 响应数据：可以是任意类型，空的时候就null

    // ==================== 成功响应 ====================

    // 成功（无数据），如删除操作："删除成功" 但不需要返回什么
    public static <T> Result<T> success() {
        return new Result<>(200, "操作成功", null);
    }

    // 成功（带数据），如查询操作：返回查询到的Ticket对象
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data);
    }

    // 成功（自定义消息+数据），如创建操作："工单创建成功"
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(200, message, data);
    }

    // ==================== 失败响应 ====================

    // 失败（默认错误码500），如："数据库连接失败"
    public static <T> Result<T> error(String message) {
        return new Result<>(500, message, null);
    }

    // 失败（自定义错误码），如：404 "工单不存在"
    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message, null);
    }
}
