-- ============================================================
-- 共享自习室分时预约管理系统 数据库初始化脚本
-- 数据库名: study_room_reservation
-- ============================================================

CREATE DATABASE IF NOT EXISTS `study_room_reservation` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `study_room_reservation`;

-- ============================================================
-- 1. 删除外卖专属表（无复用价值）
-- ============================================================
DROP TABLE IF EXISTS `shopping_cart`;
DROP TABLE IF EXISTS `address_book`;
DROP TABLE IF EXISTS `rider`;
DROP TABLE IF EXISTS `dish_flavor`;
DROP TABLE IF EXISTS `setmeal_dish`;
DROP TABLE IF EXISTS `order_detail`;

-- ============================================================
-- 2. 删除改造前的旧表（确保干净重建）
-- ============================================================
DROP TABLE IF EXISTS `orders`;
DROP TABLE IF EXISTS `seat`;
DROP TABLE IF EXISTS `reservation_package`;
DROP TABLE IF EXISTS `area`;
DROP TABLE IF EXISTS `employee`;
DROP TABLE IF EXISTS `user`;
DROP TABLE IF EXISTS `category`;
DROP TABLE IF EXISTS `dish`;
DROP TABLE IF EXISTS `setmeal`;

-- ============================================================
-- 3. 区域表（原 category → area）
-- ============================================================
CREATE TABLE `area` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name`        VARCHAR(32)  NOT NULL                COMMENT '区域名称（如静音区、沉浸区、靠窗区）',
  `sort`        INT          DEFAULT 0               COMMENT '排序字段',
  `status`      TINYINT      NOT NULL DEFAULT 1      COMMENT '状态：0停用 1启用',
  `description` VARCHAR(255) DEFAULT NULL             COMMENT '区域描述（区域特点、使用规则）',
  `seat_count`  INT          DEFAULT 0               COMMENT '区域座位总数（统计字段）',
  `create_time` DATETIME     NOT NULL                COMMENT '创建时间',
  `update_time` DATETIME     NOT NULL                COMMENT '更新时间',
  `create_user` BIGINT       NOT NULL                COMMENT '创建人ID',
  `update_user` BIGINT       NOT NULL                COMMENT '修改人ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_area_name` (`name`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='自习室区域表';

-- ============================================================
-- 4. 座位表（原 dish → seat）
-- ============================================================
CREATE TABLE `seat` (
  `id`               BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `seat_code`        VARCHAR(32)   NOT NULL                COMMENT '座位编号（如A01、B05，区域内唯一）',
  `area_id`          BIGINT        NOT NULL                COMMENT '所属区域ID，关联area表主键',
  `hour_price`       DECIMAL(10,2) NOT NULL               COMMENT '小时单价（单位：元）',
  `seat_image`       VARCHAR(255)  DEFAULT NULL            COMMENT '座位实景图URL',
  `seat_description` VARCHAR(255)  DEFAULT NULL            COMMENT '座位描述（靠窗、带插座、宽敞等）',
  `status`           TINYINT       NOT NULL DEFAULT 1       COMMENT '状态：0停用 1启用',
  `has_socket`       TINYINT       NOT NULL DEFAULT 0      COMMENT '是否带插座：0否 1是',
  `has_window`       TINYINT       NOT NULL DEFAULT 0      COMMENT '是否靠窗：0否 1是',
  `create_time`      DATETIME      NOT NULL                COMMENT '创建时间',
  `update_time`      DATETIME      NOT NULL                COMMENT '更新时间',
  `create_user`      BIGINT        NOT NULL                COMMENT '创建人ID',
  `update_user`      BIGINT        NOT NULL                COMMENT '修改人ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_area_seat_code` (`area_id`, `seat_code`),
  KEY `idx_area_id` (`area_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='自习室座位表';

-- ============================================================
-- 5. 预约套餐表（原 setmeal → reservation_package）
-- ============================================================
CREATE TABLE `reservation_package` (
  `id`             BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `area_id`        BIGINT        NOT NULL DEFAULT 0      COMMENT '适用区域ID，0表示全场通用',
  `package_name`   VARCHAR(32)   NOT NULL                COMMENT '套餐名称（如单日畅学卡、周卡、月卡）',
  `package_price`  DECIMAL(10,2) NOT NULL                COMMENT '套餐售价（单位：元）',
  `status`         TINYINT       NOT NULL DEFAULT 1      COMMENT '状态：0停用 1启用',
  `package_desc`   VARCHAR(255) DEFAULT NULL             COMMENT '套餐使用规则说明',
  `package_image`  VARCHAR(255) DEFAULT NULL             COMMENT '套餐封面图URL',
  `duration_hours` INT          DEFAULT NULL             COMMENT '套餐总有效时长（单位：小时，按时长计费套餐使用）',
  `valid_days`     INT          DEFAULT NULL             COMMENT '套餐有效天数（单位：天，天卡/周卡/月卡使用，自购买日起算）',
  `create_time`    DATETIME      NOT NULL                COMMENT '创建时间',
  `update_time`    DATETIME      NOT NULL                COMMENT '更新时间',
  `create_user`    BIGINT        NOT NULL                COMMENT '创建人ID',
  `update_user`    BIGINT        NOT NULL                COMMENT '修改人ID',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`),
  KEY `idx_area_id` (`area_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预约套餐表';

-- ============================================================
-- 6. 预约订单表（原 orders，业务语义变更为「预约订单」）
-- 状态枚举：1待支付 2已预约(待签到) 3使用中(已签到) 4已完成 5已取消 6已过期
-- ============================================================
CREATE TABLE `orders` (
  `id`             BIGINT        NOT NULL AUTO_INCREMENT COMMENT '预约订单主键ID',
  `number`         VARCHAR(50)   NOT NULL                COMMENT '预约订单号',
  `status`         TINYINT       NOT NULL DEFAULT 1      COMMENT '预约订单状态：1待支付 2已预约 3使用中 4已完成 5已取消 6已过期',
  `user_id`        BIGINT        NOT NULL                COMMENT '下单用户ID',
  `seat_id`        BIGINT        NOT NULL                COMMENT '预约座位ID，关联seat表主键',
  `area_id`        BIGINT        NOT NULL                COMMENT '所属区域ID，关联area表主键',
  `start_time`     DATETIME      NOT NULL                COMMENT '预约开始时间',
  `end_time`       DATETIME      NOT NULL                COMMENT '预约结束时间',
  `checkin_time`   DATETIME      DEFAULT NULL            COMMENT '实际签到时间',
  `checkin_code`   VARCHAR(32)   DEFAULT NULL            COMMENT '6位数字签到核销码，支付成功后生成',
  `package_id`     BIGINT        DEFAULT NULL            COMMENT '使用的预约套餐ID，按时计费则为NULL',
  `amount`         DECIMAL(10,2) NOT NULL                COMMENT '预约总金额',
  `pay_method`     TINYINT       DEFAULT NULL            COMMENT '支付方式：1微信支付',
  `pay_status`     TINYINT       NOT NULL DEFAULT 0      COMMENT '支付状态：0未支付 1已支付',
  `order_time`     DATETIME      NOT NULL                COMMENT '提交预约时间',
  `checkout_time`  DATETIME      DEFAULT NULL            COMMENT '支付完成时间',
  `cancel_time`    DATETIME      DEFAULT NULL            COMMENT '取消时间',
  `cancel_reason`  VARCHAR(255)  DEFAULT NULL            COMMENT '取消原因',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_number` (`number`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_seat_id` (`seat_id`),
  KEY `idx_status` (`status`),
  KEY `idx_start_time` (`start_time`),
  KEY `idx_end_time` (`end_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预约订单表';

-- ============================================================
-- 7. 管理员表（原 employee，语义调整为「场馆运营管理员」）
-- ============================================================
CREATE TABLE `employee` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name`        VARCHAR(32)  NOT NULL                COMMENT '管理员姓名',
  `username`    VARCHAR(32)  NOT NULL                COMMENT '登录用户名',
  `password`    VARCHAR(64)  NOT NULL                COMMENT '登录密码（MD5加密）',
  `phone`       VARCHAR(11)  NOT NULL                COMMENT '手机号',
  `sex`         VARCHAR(2)   DEFAULT NULL            COMMENT '性别',
  `id_number`   VARCHAR(18)  DEFAULT NULL            COMMENT '身份证号',
  `status`      TINYINT      NOT NULL DEFAULT 1      COMMENT '状态：0停用 1启用',
  `create_time` DATETIME     NOT NULL                COMMENT '创建时间',
  `update_time` DATETIME     NOT NULL                COMMENT '更新时间',
  `create_user` BIGINT       NOT NULL                COMMENT '创建人ID',
  `update_user` BIGINT       NOT NULL                COMMENT '修改人ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='场馆管理员表';

-- ============================================================
-- 8. 用户表（完全保留，无需改造）
-- ============================================================
CREATE TABLE `user` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `openid`      VARCHAR(64)  NOT NULL                COMMENT '微信openid',
  `name`        VARCHAR(32)  DEFAULT NULL            COMMENT '用户昵称',
  `phone`       VARCHAR(11)  DEFAULT NULL            COMMENT '手机号',
  `sex`         VARCHAR(2)   DEFAULT NULL            COMMENT '性别',
  `id_number`   VARCHAR(18)  DEFAULT NULL            COMMENT '身份证号',
  `avatar`      VARCHAR(255) DEFAULT NULL            COMMENT '头像',
  `create_time` DATETIME     NOT NULL                COMMENT '注册时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_openid` (`openid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ============================================================
-- 初始化数据
-- ============================================================

-- 管理员数据（密码: 123456 的MD5加密值）
INSERT INTO `employee` (`name`, `username`, `password`, `phone`, `sex`, `id_number`, `status`, `create_time`, `update_time`, `create_user`, `update_user`)
VALUES
('管理员', 'admin', 'e10adc3949ba59abbe56e057f20f883e', '13800000000', '1', '110101199001011234', 1, NOW(), NOW(), 1, 1);

-- 区域数据
INSERT INTO `area` (`name`, `sort`, `status`, `description`, `seat_count`, `create_time`, `update_time`, `create_user`, `update_user`)
VALUES
('静音区', 1, 1, '绝对安静，禁止交谈，适合深度学习与考研备考', 5, NOW(), NOW(), 1, 1),
('沉浸区', 2, 1, '沉浸式学习空间，配备隔音设施，适合长时间专注学习', 5, NOW(), NOW(), 1, 1),
('靠窗区', 3, 1, '靠窗位置，自然光充足，视野开阔，适合阅读与思考', 3, NOW(), NOW(), 1, 1),
('研讨区', 4, 1, '允许小声讨论，配备白板，适合小组学习与项目讨论', 3, NOW(), NOW(), 1, 1);

-- 座位数据（静音区 area_id=1, 5个座位）
INSERT INTO `seat` (`seat_code`, `area_id`, `hour_price`, `seat_image`, `seat_description`, `status`, `has_socket`, `has_window`, `create_time`, `update_time`, `create_user`, `update_user`)
VALUES
('A01', 1, 5.00, NULL, '靠门位置，带插座，光线充足', 1, 1, 0, NOW(), NOW(), 1, 1),
('A02', 1, 5.00, NULL, '中间位置，带插座，安静舒适', 1, 1, 0, NOW(), NOW(), 1, 1),
('A03', 1, 5.00, NULL, '靠墙位置，带插座，空间宽敞', 1, 1, 0, NOW(), NOW(), 1, 1),
('A04', 1, 5.00, NULL, '角落位置，无插座，极度安静', 1, 0, 0, NOW(), NOW(), 1, 1),
('A05', 1, 5.00, NULL, '靠窗位置，带插座，自然光', 1, 1, 1, NOW(), NOW(), 1, 1),

-- 沉浸区 area_id=2, 5个座位
('B01', 2, 6.00, NULL, '沉浸式隔间，带插座，独立照明', 1, 1, 0, NOW(), NOW(), 1, 1),
('B02', 2, 6.00, NULL, '沉浸式隔间，带插座，隔音设施', 1, 1, 0, NOW(), NOW(), 1, 1),
('B03', 2, 6.00, NULL, '沉浸式隔间，带插座，独立照明', 1, 1, 0, NOW(), NOW(), 1, 1),
('B04', 2, 6.00, NULL, '沉浸式隔间，无插座，极度安静', 1, 0, 0, NOW(), NOW(), 1, 1),
('B05', 2, 6.00, NULL, '沉浸式隔间，靠窗位置，自然光', 1, 1, 1, NOW(), NOW(), 1, 1),

-- 靠窗区 area_id=3, 3个座位
('C01', 3, 8.00, NULL, '落地窗旁，带插座，视野开阔', 1, 1, 1, NOW(), NOW(), 1, 1),
('C02', 3, 8.00, NULL, '落地窗旁，带插座，阳光充足', 1, 1, 1, NOW(), NOW(), 1, 1),
('C03', 3, 8.00, NULL, '落地窗旁，无插座，安静舒适', 1, 0, 1, NOW(), NOW(), 1, 1),

-- 研讨区 area_id=4, 3个座位
('D01', 4, 10.00, NULL, '研讨桌，带插座，配白板，可小声讨论', 1, 1, 0, NOW(), NOW(), 1, 1),
('D02', 4, 10.00, NULL, '研讨桌，带插座，配白板，可小声讨论', 1, 1, 0, NOW(), NOW(), 1, 1),
('D03', 4, 10.00, NULL, '研讨桌，靠窗位置，配白板', 1, 1, 1, NOW(), NOW(), 1, 1);

-- 预约套餐数据
INSERT INTO `reservation_package` (`area_id`, `package_name`, `package_price`, `status`, `package_desc`, `package_image`, `duration_hours`, `valid_days`, `create_time`, `update_time`, `create_user`, `update_user`)
VALUES
(0, '单日畅学卡', 30.00, 1, '当日有效，不限区域与座位，先到先得。适合临时自习需求。', NULL, NULL, 1, NOW(), NOW(), 1, 1),
(0, '周卡', 150.00, 1, '自购买之日起7天内有效，不限区域与座位，每日需重新预约。', NULL, NULL, 7, NOW(), NOW(), 1, 1),
(0, '月卡', 500.00, 1, '自购买之日起30天内有效，不限区域与座位，每日需重新预约。', NULL, NULL, 30, NOW(), NOW(), 1, 1),
(0, '2小时体验卡', 8.00, 1, '2小时按时计费，适合短时学习体验。', NULL, 2, NULL, NOW(), NOW(), 1, 1),
(0, '4小时专注卡', 15.00, 1, '4小时按时计费，适合半日专注学习。', NULL, 4, NULL, NOW(), NOW(), 1, 1),
(0, '8小时全天卡', 25.00, 1, '8小时按时计费，适合全天学习。', NULL, 8, NULL, NOW(), NOW(), 1, 1);

-- 更新区域座位总数
UPDATE `area` SET `seat_count` = 5 WHERE `id` = 1;
UPDATE `area` SET `seat_count` = 5 WHERE `id` = 2;
UPDATE `area` SET `seat_count` = 3 WHERE `id` = 3;
UPDATE `area` SET `seat_count` = 3 WHERE `id` = 4;
