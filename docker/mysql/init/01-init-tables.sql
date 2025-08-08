-- 创建数据库
CREATE DATABASE IF NOT EXISTS shortlink DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE shortlink;

-- 用户表分片
CREATE TABLE `sl_user_0` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `username` varchar(256) DEFAULT NULL COMMENT '用户名',
  `password` varchar(512) DEFAULT NULL COMMENT '密码',
  `real_name` varchar(256) DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(128) DEFAULT NULL COMMENT '手机号',
  `mail` varchar(512) DEFAULT NULL COMMENT '邮箱',
  `avatar` varchar(256) DEFAULT NULL COMMENT '头像',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_time` datetime DEFAULT NULL COMMENT '删除时间',
  `del_flag` tinyint(1) DEFAULT '0' COMMENT '删除标识 0：未删除 1：已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_unique_username` (`username`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `sl_user_1` LIKE `sl_user_0`;
CREATE TABLE `sl_user_2` LIKE `sl_user_0`;
CREATE TABLE `sl_user_3` LIKE `sl_user_0`;

-- 分组表分片
CREATE TABLE `sl_group_0` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `gid` varchar(32) DEFAULT NULL COMMENT '分组ID',
  `name` varchar(64) DEFAULT NULL COMMENT '分组名称',
  `username` varchar(256) DEFAULT NULL COMMENT '创建分组用户名',
  `sort_order` int(3) DEFAULT NULL COMMENT '分组排序',
  `tag` varchar(64) DEFAULT NULL COMMENT '标签',
  `description` varchar(512) DEFAULT NULL COMMENT '描述',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_time` datetime DEFAULT NULL COMMENT '删除时间',
  `del_flag` tinyint(1) DEFAULT '0' COMMENT '删除标识 0：未删除 1：已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_unique_username_gid` (`username`,`gid`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `sl_group_1` LIKE `sl_group_0`;
CREATE TABLE `sl_group_2` LIKE `sl_group_0`;
CREATE TABLE `sl_group_3` LIKE `sl_group_0`;

-- 短链接表分片
CREATE TABLE `sl_link_0` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `gid` varchar(32) DEFAULT NULL COMMENT '分组标识',
  `origin_url` varchar(1024) DEFAULT NULL COMMENT '原始链接',
  `domain` varchar(128) DEFAULT NULL COMMENT '域名',
  `short_uri` varchar(8) DEFAULT NULL COMMENT '短链接',
  `full_short_url` varchar(128) DEFAULT NULL COMMENT '完整短链接',
  `favicon` varchar(256) DEFAULT NULL COMMENT '网站图标',
  `created_type` tinyint(1) DEFAULT NULL COMMENT '创建类型 0：接口创建 1：控制台创建',
  `valid_date_type` tinyint(1) DEFAULT NULL COMMENT '有效期类型 0：永久有效 1：自定义',
  `valid_date` datetime DEFAULT NULL COMMENT '有效期',
  `expire_time` datetime DEFAULT NULL COMMENT '过期时间',
  `description` varchar(1024) DEFAULT NULL COMMENT '描述',
  `total_pv` int(11) DEFAULT '0' COMMENT '历史PV',
  `total_uv` int(11) DEFAULT '0' COMMENT '历史UV',
  `total_uip` int(11) DEFAULT '0' COMMENT '历史UIP',
  `enable_status` tinyint(1) DEFAULT '1' COMMENT '启用标识 0：未启用 1：已启用',
  `username` varchar(256) DEFAULT NULL COMMENT '创建用户名',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_time` datetime DEFAULT NULL COMMENT '删除时间',
  `del_flag` tinyint(1) DEFAULT '0' COMMENT '删除标识 0：未删除 1：已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_unique_full_short_url` (`full_short_url`,`gid`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `sl_link_1` LIKE `sl_link_0`;
CREATE TABLE `sl_link_2` LIKE `sl_link_0`;
CREATE TABLE `sl_link_3` LIKE `sl_link_0`;
CREATE TABLE `sl_link_4` LIKE `sl_link_0`;
CREATE TABLE `sl_link_5` LIKE `sl_link_0`;
CREATE TABLE `sl_link_6` LIKE `sl_link_0`;
CREATE TABLE `sl_link_7` LIKE `sl_link_0`;
CREATE TABLE `sl_link_8` LIKE `sl_link_0`;
CREATE TABLE `sl_link_9` LIKE `sl_link_0`;
CREATE TABLE `sl_link_10` LIKE `sl_link_0`;
CREATE TABLE `sl_link_11` LIKE `sl_link_0`;
CREATE TABLE `sl_link_12` LIKE `sl_link_0`;
CREATE TABLE `sl_link_13` LIKE `sl_link_0`;
CREATE TABLE `sl_link_14` LIKE `sl_link_0`;
CREATE TABLE `sl_link_15` LIKE `sl_link_0`;

-- 短链接路由表分片
CREATE TABLE `sl_link_router_0` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `full_short_url` varchar(128) DEFAULT NULL COMMENT '完整短链接',
  `gid` varchar(32) DEFAULT NULL COMMENT '分组标识',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_time` datetime DEFAULT NULL COMMENT '删除时间',
  `del_flag` tinyint(1) DEFAULT '0' COMMENT '删除标识 0：未删除 1：已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_unique_full_short_url` (`full_short_url`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `sl_link_router_1` LIKE `sl_link_router_0`;
CREATE TABLE `sl_link_router_2` LIKE `sl_link_router_0`;
CREATE TABLE `sl_link_router_3` LIKE `sl_link_router_0`;
CREATE TABLE `sl_link_router_4` LIKE `sl_link_router_0`;
CREATE TABLE `sl_link_router_5` LIKE `sl_link_router_0`;
CREATE TABLE `sl_link_router_6` LIKE `sl_link_router_0`;
CREATE TABLE `sl_link_router_7` LIKE `sl_link_router_0`;
CREATE TABLE `sl_link_router_8` LIKE `sl_link_router_0`;
CREATE TABLE `sl_link_router_9` LIKE `sl_link_router_0`;
CREATE TABLE `sl_link_router_10` LIKE `sl_link_router_0`;
CREATE TABLE `sl_link_router_11` LIKE `sl_link_router_0`;
CREATE TABLE `sl_link_router_12` LIKE `sl_link_router_0`;
CREATE TABLE `sl_link_router_13` LIKE `sl_link_router_0`;
CREATE TABLE `sl_link_router_14` LIKE `sl_link_router_0`;
CREATE TABLE `sl_link_router_15` LIKE `sl_link_router_0`;

-- 短链接访问统计表
CREATE TABLE `sl_link_access_stats_0` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `full_short_url` varchar(128) DEFAULT NULL COMMENT '完整短链接',
  `date` date DEFAULT NULL COMMENT '日期',
  `hour` int(11) DEFAULT NULL COMMENT '小时',
  `weekday` int(11) DEFAULT NULL COMMENT '星期',
  `pv` int(11) DEFAULT '0' COMMENT '访问量',
  `uv` int(11) DEFAULT '0' COMMENT '独立访客数',
  `uip` int(11) DEFAULT '0' COMMENT '独立IP数',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_time` datetime DEFAULT NULL COMMENT '删除时间',
  `del_flag` tinyint(1) DEFAULT '0' COMMENT '删除标识 0：未删除 1：已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_unique_access_stats` (`full_short_url`,`date`,`hour`,`weekday`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `sl_link_access_stats_1` LIKE `sl_link_access_stats_0`;
CREATE TABLE `sl_link_access_stats_2` LIKE `sl_link_access_stats_0`;
CREATE TABLE `sl_link_access_stats_3` LIKE `sl_link_access_stats_0`;
CREATE TABLE `sl_link_access_stats_4` LIKE `sl_link_access_stats_0`;
CREATE TABLE `sl_link_access_stats_5` LIKE `sl_link_access_stats_0`;
CREATE TABLE `sl_link_access_stats_6` LIKE `sl_link_access_stats_0`;
CREATE TABLE `sl_link_access_stats_7` LIKE `sl_link_access_stats_0`;
CREATE TABLE `sl_link_access_stats_8` LIKE `sl_link_access_stats_0`;
CREATE TABLE `sl_link_access_stats_9` LIKE `sl_link_access_stats_0`;
CREATE TABLE `sl_link_access_stats_10` LIKE `sl_link_access_stats_0`;
CREATE TABLE `sl_link_access_stats_11` LIKE `sl_link_access_stats_0`;
CREATE TABLE `sl_link_access_stats_12` LIKE `sl_link_access_stats_0`;
CREATE TABLE `sl_link_access_stats_13` LIKE `sl_link_access_stats_0`;
CREATE TABLE `sl_link_access_stats_14` LIKE `sl_link_access_stats_0`;
CREATE TABLE `sl_link_access_stats_15` LIKE `sl_link_access_stats_0`;

-- 创建默认管理员用户
INSERT INTO `sl_user_0` (`username`, `password`, `real_name`, `phone`, `mail`, `create_time`, `update_time`, `del_flag`)
VALUES ('admin', 'admin123', '管理员', '13800138000', 'admin@example.com', NOW(), NOW(), 0);

-- 创建默认分组
INSERT INTO `sl_group_0` (`gid`, `name`, `username`, `sort_order`, `description`, `create_time`, `update_time`, `del_flag`)
VALUES (UUID(), '默认分组', 'admin', 0, '系统默认分组', NOW(), NOW(), 0); 