-- =====================================================
-- 工单系统 - 测试数据（schema.sql 执行完后自动执行）
-- 使用 INSERT IGNORE 保证重复执行不报错
-- =====================================================

INSERT IGNORE INTO `user` (`id`, `username`, `role`) VALUES
(1, '张三', 'user'),
(2, '李四', 'agent'),
(3, '王五', 'admin');

INSERT IGNORE INTO `ticket` (`title`, `description`, `status`, `priority`, `category`, `creator_id`) VALUES
('无法登录系统', '点击登录按钮后页面无响应，Chrome 最新版本', '待处理', '高', '账号问题', 1),
('订单数据导出报错', '导出数据时提示"服务器内部错误"', '处理中', '紧急', '数据问题', 1);
