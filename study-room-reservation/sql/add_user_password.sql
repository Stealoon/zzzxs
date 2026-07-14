-- 用户表添加password字段
ALTER TABLE `user` ADD COLUMN `password` VARCHAR(100) DEFAULT NULL COMMENT '密码(MD5)' AFTER `phone`;

-- 插入测试用户（密码为123456的MD5值）
INSERT INTO `user` (`openid`, `name`, `phone`, `password`, `sex`, `create_time`) VALUES ('test_user_001', '测试用户', '13800138000', 'e10adc3949ba59abbe56e057f20f883e', '1', NOW());
