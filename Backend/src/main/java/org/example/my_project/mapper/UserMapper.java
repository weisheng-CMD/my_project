package org.example.my_project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.my_project.entity.User;

/*
 * ========== 用户 Mapper（数据访问层） ==========
 *
 * 和 TicketMapper 完全一样，只是泛型参数变成 User。
 * BaseMapper<User> 自动提供对 user 表的增删改查。
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
