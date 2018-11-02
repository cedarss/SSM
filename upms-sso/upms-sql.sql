DROP TABLE T_USER;

CREATE TABLE `T_USER` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_name` varchar(60) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户名称',
  `account` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户账号',
  `password` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户密码',
  `password_random` varchar(100)   NOT NULL COMMENT '盐',
  	`email` varchar(25) NOT NULL COMMENT '邮箱',
	`phone` VARCHAR(15) NOT NULL COMMENT '手机',
	`create_time` datetime NOT NULL COMMENT '创建时间',
	`is_enable` int(1) NOT NULL COMMENT '是否启动',
	
  PRIMARY KEY (`ID`) USING BTREE,
  UNIQUE KEY `user_unique_account` (`account`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='用户表';

DROP TABLE t_db_log;

CREATE TABLE `t_db_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) DEFAULT NULL,
  `type` int(11) DEFAULT NULL,
  `url` varchar(30) DEFAULT NULL,
  `request_type` varchar(30) DEFAULT NULL,
  `request_param` varchar(200) DEFAULT NULL,
  `user` varchar(20) DEFAULT NULL,
  `ip` varchar(20) DEFAULT NULL,
  `ip_info` varchar(20) DEFAULT NULL,
  `time` int(11) DEFAULT NULL,
  `create_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=112 DEFAULT CHARSET=utf8