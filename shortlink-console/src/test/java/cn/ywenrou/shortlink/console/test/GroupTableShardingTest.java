package cn.ywenrou.shortlink.console.test;

public class GroupTableShardingTest {
    public static final String SQL = "CREATE TABLE `sl_group_%d` (\n" +
            "  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',\n" +
            "  `gid` varchar(32) DEFAULT NULL COMMENT '分组标识',\n" +
            "  `name` varchar(64) DEFAULT NULL COMMENT '分组名称',\n" +
            "  `description` varchar(256) DEFAULT NULL COMMENT '分组描述',\n" +
            "  `tag` varchar(32) DEFAULT NULL COMMENT '分组颜色/icon',\n" +
            "  `username` varchar(256) DEFAULT NULL COMMENT '创建分组用户名',\n" +
            "  `sort_order` int(3) DEFAULT NULL COMMENT '分组排序',\n" +
            "  `create_time` datetime DEFAULT NULL COMMENT '创建时间',\n" +
            "  `update_time` datetime DEFAULT NULL COMMENT '修改时间',\n" +
            "  `del_time` datetime DEFAULT NULL COMMENT '删除时间',\n" +
            "  `del_flag` tinyint(1) DEFAULT '0' COMMENT '删除标识 0：未删除 1：已删除',\n" +
            "  PRIMARY KEY (`id`),\n" +
            "  UNIQUE KEY `idx_unique_gid` (`gid`) USING BTREE,\n" +
            "  UNIQUE KEY `idx_unique_username_name` (`name`,`username`) USING BTREE\n" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";
    public static void main(String[] args) {
        for (int i = 0; i < 4; i++) {
            System.out.printf(SQL + "%n", i);
        }

    }
}
