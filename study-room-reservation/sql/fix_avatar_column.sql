-- 修复 user 表缺失 avatar 字段
-- User.java 实体已有 avatar 字段，但建表语句中遗漏，导致新用户注册时 INSERT 失败
ALTER TABLE `user` ADD COLUMN `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像' AFTER `id_number`;
