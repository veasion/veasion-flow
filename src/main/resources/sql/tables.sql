CREATE TABLE `flow_default_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `flow` varchar(30) NOT NULL COMMENT '流程',
  `start_node` varchar(30) NOT NULL COMMENT '开始节点',
  `error_node` varchar(30) DEFAULT NULL COMMENT '错误节点',
  `is_deleted` tinyint(2) NULL DEFAULT 0 COMMENT '删除状态: 1 已删除 0 未删除',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='流程默认配置';

CREATE TABLE `flow_node_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `code` varchar(30) NOT NULL COMMENT '节点',
  `name` varchar(100) NOT NULL COMMENT '节点名称',
  `is_virtual` tinyint(2) DEFAULT '0' COMMENT '是否虚拟节点：1 虚拟节点',
  `is_deleted` tinyint(2) NULL DEFAULT 0 COMMENT '删除状态: 1 已删除 0 未删除',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='流程节点配置';

CREATE TABLE `flow_next_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `flow` varchar(30) NOT NULL COMMENT '流程',
  `node` varchar(30) NOT NULL COMMENT '节点',
  `next_flow` varchar(30) DEFAULT NULL COMMENT '下一个流程',
  `next_node` varchar(30) DEFAULT NULL COMMENT '下一个流程节点',
  `cond` varchar(255) DEFAULT NULL COMMENT '条件（脚本）',
  `on_before` varchar(255) DEFAULT NULL COMMENT '节点前执行（脚本）',
  `on_after` varchar(255) DEFAULT NULL COMMENT '节点后执行（脚本）',
  `is_deleted` tinyint(2) NULL DEFAULT 0 COMMENT '删除状态: 1 已删除 0 未删除',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='流程配置';

CREATE TABLE `flow_run` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `flow` varchar(30) NOT NULL COMMENT '流程',
  `flow_code` varchar(30) NOT NULL COMMENT '流程编码',
  `node` varchar(30) DEFAULT NULL COMMENT '当前运行节点',
  `run_data` text DEFAULT NULL COMMENT '运行数据',
  `status` tinyint(4) DEFAULT '0' COMMENT '流程状态：1正常，2结束，3暂停，4错误',
  `is_deleted` tinyint(2) NULL DEFAULT 0 COMMENT '删除状态: 1 已删除 0 未删除',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `idx_flow`(`flow`, `flow_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='流程运行';

CREATE TABLE `flow_run_track` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `flow` varchar(30) NOT NULL COMMENT '流程',
  `flow_code` varchar(30) NOT NULL COMMENT '流程编码',
  `node` varchar(30) DEFAULT NULL COMMENT '节点',
  `track_data` text DEFAULT NULL COMMENT '跟踪数据',
  `exec_time_millis` bigint(16) DEFAULT NULL COMMENT '执行时间(毫秒)',
  `is_deleted` tinyint(2) NULL DEFAULT 0 COMMENT '删除状态: 1 已删除 0 未删除',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `idx_flow`(`flow`, `flow_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='流程运行流水';
