-- ============================================================
-- ESB 前台管理控制台管理表（xrpc-admin）
-- 对应 xrpc-admin/src/main/java/com/xs/admin/entity/*
-- ============================================================

USE `esb_platform`;

-- 1. 用户
CREATE TABLE IF NOT EXISTS `esb_admin_user` (
  `id`             INT NOT NULL AUTO_INCREMENT,
  `username`       VARCHAR(64)  NOT NULL COMMENT '登录账号',
  `password_hash`  VARCHAR(128) NOT NULL COMMENT 'BCrypt 密码哈希',
  `salt`           VARCHAR(64)  DEFAULT NULL,
  `real_name`      VARCHAR(64)  DEFAULT NULL COMMENT '姓名',
  `status`         VARCHAR(8)   DEFAULT 'ENABLE' COMMENT 'ENABLE/DISABLE',
  `last_login_time` VARCHAR(32) DEFAULT NULL,
  `create_time`    VARCHAR(32)  DEFAULT NULL,
  `update_time`    VARCHAR(32)  DEFAULT NULL,
  `roles`          VARCHAR(255) DEFAULT NULL COMMENT '角色编码，逗号分隔；ADMIN 拥有全部权限',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_admin_user_name` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='控制台用户';

-- 2. 角色
CREATE TABLE IF NOT EXISTS `esb_admin_role` (
  `id`        INT NOT NULL AUTO_INCREMENT,
  `role_code` VARCHAR(64) NOT NULL,
  `role_name` VARCHAR(64) DEFAULT NULL,
  `remark`    VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_admin_role_code` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色';

-- 3. 用户-角色
CREATE TABLE IF NOT EXISTS `esb_admin_user_role` (
  `user_id` INT NOT NULL,
  `role_id` INT NOT NULL,
  PRIMARY KEY (`user_id`,`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户角色关联';

-- 4. 权限（菜单 / 按钮）
CREATE TABLE IF NOT EXISTS `esb_admin_permission` (
  `id`         INT NOT NULL AUTO_INCREMENT,
  `parent_id`  INT DEFAULT 0,
  `perm_code`  VARCHAR(64) NOT NULL COMMENT '权限码，如 menu:tag',
  `perm_name`  VARCHAR(64) DEFAULT NULL,
  `type`       VARCHAR(16) DEFAULT NULL COMMENT 'menu/button',
  `path`       VARCHAR(128) DEFAULT NULL,
  `component`  VARCHAR(128) DEFAULT NULL,
  `sort`       INT DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_admin_perm_code` (`perm_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='权限点';

-- 5. 角色-权限
CREATE TABLE IF NOT EXISTS `esb_admin_role_permission` (
  `role_id`       INT NOT NULL,
  `permission_id` INT NOT NULL,
  PRIMARY KEY (`role_id`,`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色权限关联';

-- 6. 操作审计
CREATE TABLE IF NOT EXISTS `esb_admin_audit_log` (
  `id`           BIGINT NOT NULL AUTO_INCREMENT,
  `operator`     VARCHAR(64) DEFAULT NULL,
  `operate_time` VARCHAR(32) DEFAULT NULL,
  `ip`           VARCHAR(64) DEFAULT NULL,
  `module`       VARCHAR(64) DEFAULT NULL,
  `action`       VARCHAR(64) DEFAULT NULL,
  `target`       VARCHAR(128) DEFAULT NULL,
  `before_json`  TEXT,
  `after_json`   TEXT,
  `result`       VARCHAR(16) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_audit_time` (`operate_time`),
  KEY `idx_audit_operator` (`operator`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='操作审计日志';

-- 7. 配置版本（草稿 / 审批 / 发布 / 回滚）
CREATE TABLE IF NOT EXISTS `esb_config_version` (
  `id`           BIGINT NOT NULL AUTO_INCREMENT,
  `env`          VARCHAR(32) DEFAULT NULL COMMENT 'dev/test/prod',
  `target_type`  VARCHAR(64) DEFAULT NULL COMMENT 'CUSTOMER_REQUEST 等',
  `target_key`   VARCHAR(128) DEFAULT NULL COMMENT '交易码',
  `version_no`   INT DEFAULT NULL,
  `status`       VARCHAR(24) DEFAULT NULL COMMENT 'DRAFT/REVIEWING/APPROVED/PUBLISHED/ROLLED_BACK',
  `content_json` LONGTEXT COMMENT '配置快照',
  `diff_json`    LONGTEXT COMMENT '差异',
  `draft_by`     VARCHAR(64) DEFAULT NULL,
  `approve_by`   VARCHAR(64) DEFAULT NULL,
  `publish_time` VARCHAR(32) DEFAULT NULL,
  `remark`       VARCHAR(255) DEFAULT NULL,
  `create_time`  VARCHAR(32) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_version_target` (`env`,`target_type`,`target_key`),
  KEY `idx_version_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='配置版本';

-- 8. 错误码字典
CREATE TABLE IF NOT EXISTS `esb_error_code` (
  `id`        INT NOT NULL AUTO_INCREMENT,
  `code`      VARCHAR(32) NOT NULL,
  `message`   VARCHAR(255) DEFAULT NULL,
  `module`    VARCHAR(64) DEFAULT NULL,
  `suggestion` VARCHAR(255) DEFAULT NULL,
  `status`    VARCHAR(8) DEFAULT 'ENABLE',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_error_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='错误码字典';

-- 9. 报文样例
CREATE TABLE IF NOT EXISTS `esb_msg_sample` (
  `id`          INT NOT NULL AUTO_INCREMENT,
  `tran_no`     VARCHAR(64) DEFAULT NULL,
  `sample_type` VARCHAR(32) DEFAULT NULL COMMENT 'REQUEST/RESPONSE',
  `content`     LONGTEXT,
  `remark`      VARCHAR(255) DEFAULT NULL,
  `create_time` VARCHAR(32) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_sample_tran` (`tran_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='黄金报文样例';

-- 10. 密钥管理（仅存指纹与密文引用，不存明文）
CREATE TABLE IF NOT EXISTS `esb_key_store` (
  `id`          INT NOT NULL AUTO_INCREMENT,
  `channel`     VARCHAR(64) DEFAULT NULL,
  `key_type`    VARCHAR(32) DEFAULT NULL COMMENT 'SM2/SM4',
  `fingerprint` VARCHAR(128) DEFAULT NULL,
  `cipher_ref`  VARCHAR(512) DEFAULT NULL COMMENT 'KMS 引用',
  `status`      VARCHAR(16) DEFAULT 'ENABLE',
  `expire_time` VARCHAR(32) DEFAULT NULL,
  `create_time` VARCHAR(32) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_key_channel` (`channel`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='密钥元信息';

-- 11. TCP 端口映射（替代 cPort.json）
CREATE TABLE IF NOT EXISTS `esb_cport_config` (
  `id`          INT NOT NULL AUTO_INCREMENT,
  `trannos`     VARCHAR(512) DEFAULT NULL COMMENT '交易码列表，逗号分隔',
  `address`     VARCHAR(128) DEFAULT NULL,
  `port`        INT DEFAULT NULL,
  `charset`     VARCHAR(32) DEFAULT NULL,
  `fixedlength` INT DEFAULT NULL COMMENT '长度位宽',
  `type`        VARCHAR(16) DEFAULT NULL COMMENT 'DC/JSON/XML',
  `str1`        VARCHAR(255) DEFAULT NULL,
  `str2`        VARCHAR(255) DEFAULT NULL,
  `str3`        VARCHAR(255) DEFAULT NULL,
  `str4`        VARCHAR(255) DEFAULT NULL,
  `str5`        VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='TCP 端口映射';

-- 12. 日志开关（替代 logConfig.json）
CREATE TABLE IF NOT EXISTS `esb_log_switch` (
  `id`       INT NOT NULL AUTO_INCREMENT,
  `log_type` VARCHAR(8) NOT NULL COMMENT '01~08',
  `enabled`  VARCHAR(8) DEFAULT 'OFF' COMMENT 'OK/OFF',
  `remark`   VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_log_type` (`log_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='日志类型开关';
