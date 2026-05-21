-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id`        BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键 ID',
    `username`  VARCHAR(50)     NOT NULL                 COMMENT '用户名',
    `password`  VARCHAR(255)    NOT NULL DEFAULT ''      COMMENT '密码（BCrypt 加密）',
    `role`      VARCHAR(20)     NOT NULL DEFAULT 'user'  COMMENT '角色：user / agent / admin',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 工单表
CREATE TABLE IF NOT EXISTS `ticket` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT                         COMMENT '主键 ID',
    `title`       VARCHAR(255) NOT NULL                                        COMMENT '工单标题',
    `description` TEXT                                                         COMMENT '工单描述',
    `status`      VARCHAR(20)  NOT NULL DEFAULT '待处理'                        COMMENT '状态',
    `priority`    VARCHAR(10)  NOT NULL DEFAULT '中'                            COMMENT '优先级',
    `category`    VARCHAR(50)                                                  COMMENT '分类',
    `creator_id`  BIGINT                                                       COMMENT '创建人 ID',
    `assignee_id` BIGINT                                                       COMMENT '处理人 ID',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP                       COMMENT '创建时间',
    `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     TINYINT      DEFAULT 0                                       COMMENT '逻辑删除（0=正常，1=已删除）',
    PRIMARY KEY (`id`),
    INDEX `idx_status`    (`status`),
    INDEX `idx_creator`   (`creator_id`),
    INDEX `idx_assignee`  (`assignee_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工单表';
