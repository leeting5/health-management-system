USE health_db;

UPDATE `sys_user`
SET `nickname` = '系统管理员'
WHERE `id` = 2;

UPDATE `sys_user`
SET `nickname` = '演示用户一'
WHERE `id` = 3;

UPDATE `sys_user`
SET `nickname` = '演示用户二'
WHERE `id` = 4;
