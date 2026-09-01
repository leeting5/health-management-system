USE health_db;

CREATE TABLE IF NOT EXISTS `operation_log` (
    `id`            BIGINT        NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    `user_id`       BIGINT        DEFAULT NULL COMMENT '用户ID',
    `username`      VARCHAR(50)   DEFAULT NULL COMMENT '用户名',
    `operation`     VARCHAR(100)  NOT NULL COMMENT '操作内容',
    `method`        VARCHAR(10)   DEFAULT NULL COMMENT 'HTTP方法',
    `path`          VARCHAR(255)  DEFAULT NULL COMMENT '请求路径',
    `ip`            VARCHAR(50)   DEFAULT NULL COMMENT '客户端IP',
    `success`       TINYINT       DEFAULT 1 COMMENT '是否成功：0-失败，1-成功',
    `error_message` VARCHAR(500)  DEFAULT NULL COMMENT '失败信息',
    `create_time`   DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    PRIMARY KEY (`id`),
    KEY `idx_log_create_time` (`create_time`),
    KEY `idx_log_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

CREATE TABLE IF NOT EXISTS `announcement` (
    `id`          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '公告ID',
    `title`       VARCHAR(100)  NOT NULL COMMENT '公告标题',
    `content`     TEXT          NOT NULL COMMENT '公告内容',
    `status`      TINYINT       DEFAULT 0 COMMENT '状态：0-草稿，1-已发布',
    `publisher`   VARCHAR(50)   DEFAULT NULL COMMENT '发布人',
    `create_time` DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     TINYINT       DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_announcement_status` (`status`, `create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统公告表';
