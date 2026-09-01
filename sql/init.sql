-- =============================================
-- 个人健康管理系统 数据库初始化脚本
-- 数据库：health_db
-- 字符集：utf8mb4
-- =============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS health_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE health_db;

-- =============================================
-- 用户表
-- =============================================
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username`    VARCHAR(50)  NOT NULL COMMENT '用户名',
    `password`    VARCHAR(255) NOT NULL COMMENT '密码哈希（PBKDF2WithHmacSHA256）',
    `nickname`    VARCHAR(50)  DEFAULT NULL COMMENT '昵称',
    `gender`      TINYINT      DEFAULT 0 COMMENT '性别：0-未知，1-男，2-女',
    `age`         INT          DEFAULT NULL COMMENT '年龄',
    `phone`       VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
    `email`       VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `avatar`      VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    `role`        VARCHAR(20)  DEFAULT 'USER' COMMENT '角色：USER-普通用户，ADMIN-管理员',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     TINYINT      DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- =============================================
-- 健康记录表
-- =============================================
DROP TABLE IF EXISTS `health_record`;
CREATE TABLE `health_record` (
    `id`                  BIGINT         NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `user_id`             BIGINT         NOT NULL COMMENT '用户ID',
    `record_date`         DATE           NOT NULL COMMENT '记录日期',
    `height`              DECIMAL(5,2)   DEFAULT NULL COMMENT '身高（cm）',
    `weight`              DECIMAL(5,2)   DEFAULT NULL COMMENT '体重（kg）',
    `bmi`                 DECIMAL(4,1)   DEFAULT NULL COMMENT 'BMI指数',
    `systolic_pressure`   INT            DEFAULT NULL COMMENT '收缩压（mmHg）',
    `diastolic_pressure`  INT            DEFAULT NULL COMMENT '舒张压（mmHg）',
    `blood_sugar`         DECIMAL(5,2)   DEFAULT NULL COMMENT '血糖（mmol/L）',
    `heart_rate`          INT            DEFAULT NULL COMMENT '心率（次/分钟）',
    `remark`              VARCHAR(500)   DEFAULT NULL COMMENT '备注',
    `create_time`         DATETIME       DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`         DATETIME       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`             TINYINT        DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_record_date` (`record_date`),
    KEY `idx_user_date` (`user_id`, `record_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='健康记录表';

-- =============================================
-- AI 会话表
-- =============================================
DROP TABLE IF EXISTS `ai_conversation`;
CREATE TABLE `ai_conversation` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '会话ID',
    `user_id`     BIGINT       NOT NULL COMMENT '用户ID',
    `title`       VARCHAR(100) NOT NULL DEFAULT '新对话' COMMENT '会话名称',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     TINYINT      DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_conversation_user` (`user_id`, `update_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI会话表';

-- =============================================
-- AI 消息表
-- =============================================
DROP TABLE IF EXISTS `ai_message`;
CREATE TABLE `ai_message` (
    `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '消息ID',
    `conversation_id` BIGINT       NOT NULL COMMENT '会话ID',
    `role`            VARCHAR(20)  NOT NULL COMMENT '角色：user/assistant',
    `content`         TEXT         NOT NULL COMMENT '消息内容',
    `create_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_message_conversation` (`conversation_id`, `create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI消息表';

-- =============================================
-- 操作日志表
-- =============================================
DROP TABLE IF EXISTS `operation_log`;
CREATE TABLE `operation_log` (
    `id`           BIGINT        NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    `user_id`      BIGINT        DEFAULT NULL COMMENT '用户ID',
    `username`     VARCHAR(50)   DEFAULT NULL COMMENT '用户名',
    `operation`    VARCHAR(100)  NOT NULL COMMENT '操作内容',
    `method`       VARCHAR(10)   DEFAULT NULL COMMENT 'HTTP方法',
    `path`         VARCHAR(255)  DEFAULT NULL COMMENT '请求路径',
    `ip`           VARCHAR(50)   DEFAULT NULL COMMENT '客户端IP',
    `success`      TINYINT       DEFAULT 1 COMMENT '是否成功：0-失败，1-成功',
    `error_message` VARCHAR(500) DEFAULT NULL COMMENT '失败信息',
    `create_time`  DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    PRIMARY KEY (`id`),
    KEY `idx_log_create_time` (`create_time`),
    KEY `idx_log_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- =============================================
-- 公告表
-- =============================================
DROP TABLE IF EXISTS `announcement`;
CREATE TABLE `announcement` (
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

-- =============================================
-- 插入测试用户（密码：123456）
-- 密码哈希格式：iterations$saltBase64$hashBase64
-- =============================================
INSERT INTO `sys_user` (`id`, `username`, `password`, `nickname`, `gender`, `age`, `phone`, `email`, `role`)
VALUES
(1, 'admin', '120000$paHX8xb/4/yekQatyM0azA==$Q+2iT2AWIErEHn5mPbc+O5klZgUYBWPImZPi/d0aLg0=', '系统管理员', 1, 28, '13800000001', 'admin@example.com', 'ADMIN'),
(2, 'test', '120000$paHX8xb/4/yekQatyM0azA==$Q+2iT2AWIErEHn5mPbc+O5klZgUYBWPImZPi/d0aLg0=', '测试用户', 1, 25, '13800000002', 'test@example.com', 'USER'),
(3, 'user1', '120000$paHX8xb/4/yekQatyM0azA==$Q+2iT2AWIErEHn5mPbc+O5klZgUYBWPImZPi/d0aLg0=', '演示用户一', 2, 32, '13800000003', 'user1@example.com', 'USER'),
(4, 'user2', '120000$paHX8xb/4/yekQatyM0azA==$Q+2iT2AWIErEHn5mPbc+O5klZgUYBWPImZPi/d0aLg0=', '演示用户二', 1, 45, '13800000004', 'user2@example.com', 'USER');

-- =============================================
-- 插入测试健康数据
-- =============================================
INSERT INTO `health_record` (`user_id`, `record_date`, `height`, `weight`, `bmi`,
    `systolic_pressure`, `diastolic_pressure`, `blood_sugar`, `heart_rate`, `remark`)
VALUES
(2, DATE_SUB(CURDATE(), INTERVAL 6 DAY), 175.00, 68.00, 22.2, 118, 78, 5.20, 72, '身体状况良好'),
(2, DATE_SUB(CURDATE(), INTERVAL 5 DAY), 175.00, 68.20, 22.3, 120, 80, 5.50, 75, NULL),
(2, DATE_SUB(CURDATE(), INTERVAL 4 DAY), 175.00, 67.80, 22.1, 116, 76, 5.30, 70, NULL),
(2, DATE_SUB(CURDATE(), INTERVAL 3 DAY), 175.00, 68.50, 22.4, 122, 82, 5.80, 78, '昨天熬夜了'),
(2, DATE_SUB(CURDATE(), INTERVAL 2 DAY), 175.00, 68.00, 22.2, 119, 79, 5.40, 73, NULL),
(2, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 175.00, 67.50, 22.0, 115, 75, 5.10, 71, '晨跑3公里'),
(2, CURDATE(), 175.00, 68.10, 22.2, 118, 78, 5.30, 72, '早餐后测量'),
(3, DATE_SUB(CURDATE(), INTERVAL 4 DAY), 162.00, 52.00, 19.8, 105, 68, 4.90, 68, '饮食规律'),
(3, DATE_SUB(CURDATE(), INTERVAL 2 DAY), 162.00, 52.30, 19.9, 108, 70, 5.10, 70, NULL),
(3, CURDATE(), 162.00, 52.10, 19.8, 106, 69, 5.00, 69, '晨间散步'),
(4, DATE_SUB(CURDATE(), INTERVAL 5 DAY), 170.00, 78.00, 27.0, 135, 88, 6.20, 82, '工作压力较大'),
(4, DATE_SUB(CURDATE(), INTERVAL 3 DAY), 170.00, 77.60, 26.9, 132, 86, 6.10, 80, NULL),
(4, CURDATE(), 170.00, 77.80, 26.9, 133, 87, 6.00, 81, '注意低盐饮食');

-- =============================================
-- 数据库初始化完成
-- =============================================
