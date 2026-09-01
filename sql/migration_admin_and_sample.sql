-- =============================================
-- 管理员角色与示例数据迁移脚本
-- 适用：已经初始化过 health_db 的现有环境
-- 建议只执行一次；重复执行前请先备份数据
-- =============================================

USE health_db;

-- 补充角色字段
ALTER TABLE `sys_user`
    ADD COLUMN `role` VARCHAR(20) DEFAULT 'USER' COMMENT '角色：USER-普通用户，ADMIN-管理员' AFTER `avatar`;

-- 统一密码字段长度
ALTER TABLE `sys_user`
    MODIFY COLUMN `password` VARCHAR(255) NOT NULL COMMENT '密码哈希（PBKDF2WithHmacSHA256）';

-- 为现有测试用户补齐角色
UPDATE `sys_user`
SET `role` = 'USER'
WHERE `username` = 'test';

-- 示例用户，密码均为 123456
INSERT IGNORE INTO `sys_user`
(`id`, `username`, `password`, `nickname`, `gender`, `age`, `phone`, `email`, `role`)
VALUES
(2, 'admin', '120000$paHX8xb/4/yekQatyM0azA==$Q+2iT2AWIErEHn5mPbc+O5klZgUYBWPImZPi/d0aLg0=', '系统管理员', 1, 28, '13800000001', 'admin@example.com', 'ADMIN'),
(3, 'user1', '120000$paHX8xb/4/yekQatyM0azA==$Q+2iT2AWIErEHn5mPbc+O5klZgUYBWPImZPi/d0aLg0=', '演示用户一', 2, 32, '13800000003', 'user1@example.com', 'USER'),
(4, 'user2', '120000$paHX8xb/4/yekQatyM0azA==$Q+2iT2AWIErEHn5mPbc+O5klZgUYBWPImZPi/d0aLg0=', '演示用户二', 1, 45, '13800000004', 'user2@example.com', 'USER');

-- 清理并补充示例健康记录
DELETE FROM `health_record`
WHERE `user_id` IN (3, 4);

INSERT INTO `health_record`
(`user_id`, `record_date`, `height`, `weight`, `bmi`, `systolic_pressure`, `diastolic_pressure`, `blood_sugar`, `heart_rate`, `remark`)
VALUES
(3, DATE_SUB(CURDATE(), INTERVAL 4 DAY), 162.00, 52.00, 19.8, 105, 68, 4.90, 68, '饮食规律'),
(3, DATE_SUB(CURDATE(), INTERVAL 2 DAY), 162.00, 52.30, 19.9, 108, 70, 5.10, 70, NULL),
(3, CURDATE(), 162.00, 52.10, 19.8, 106, 69, 5.00, 69, '晨间散步'),
(4, DATE_SUB(CURDATE(), INTERVAL 5 DAY), 170.00, 78.00, 27.0, 135, 88, 6.20, 82, '工作压力较大'),
(4, DATE_SUB(CURDATE(), INTERVAL 3 DAY), 170.00, 77.60, 26.9, 132, 86, 6.10, 80, NULL),
(4, CURDATE(), 170.00, 77.80, 26.9, 133, 87, 6.00, 81, '注意低盐饮食');
