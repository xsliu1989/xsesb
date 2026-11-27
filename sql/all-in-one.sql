-- =============== 00-create-database.sql ===============
-- ============================================================
-- ESB 平台数据库（MySQL 8.x）
-- ============================================================

CREATE DATABASE IF NOT EXISTS `esb_platform`
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_general_ci;

USE `esb_platform`;


-- =============== 01-business-metadata-tables.sql ===============
-- ============================================================
-- 既有 ESB 后台的 8 张元数据 / 日志表
-- 依据 JPA 实体反推：com.xs.datamodel.* 与 com.xs.model.Tag
-- 若生产库已存在同名表，CREATE TABLE IF NOT EXISTS 不会覆盖
-- ============================================================

USE `esb_platform`;

-- 1. 渠道系统（请求发起方）
CREATE TABLE IF NOT EXISTS `esb_customer_sysname` (
  `id`      INT NOT NULL AUTO_INCREMENT,
  `systype` VARCHAR(64)  DEFAULT NULL COMMENT '渠道编号（缓存 key，如 CH01）',
  `sysname` VARCHAR(128) DEFAULT NULL COMMENT '渠道中文名称',
  `flag`    VARCHAR(8)   DEFAULT 'true' COMMENT '是否启用 true/false',
  `size`    VARCHAR(16)  DEFAULT NULL COMMENT '渠道最大并发',
  `str1`    VARCHAR(255) DEFAULT NULL,
  `str2`    VARCHAR(255) DEFAULT NULL,
  `str3`    VARCHAR(255) DEFAULT NULL,
  `str4`    VARCHAR(255) DEFAULT NULL,
  `str5`    VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_customer_sysname_systype` (`systype`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='渠道系统定义';

-- 2. 渠道接口（交易码）
CREATE TABLE IF NOT EXISTS `esb_customer_transaction_info` (
  `id`          INT NOT NULL AUTO_INCREMENT,
  `tranno`      VARCHAR(64)  DEFAULT NULL COMMENT '交易码',
  `sysname`     VARCHAR(255) DEFAULT NULL COMMENT '允许的渠道，逗号分隔',
  `clazz`       VARCHAR(128) DEFAULT NULL COMMENT '处理 Bean 名称，如 defaultService',
  `iscryption`  VARCHAR(8)   DEFAULT 'false' COMMENT '是否需要加解密',
  `isOpen`      VARCHAR(8)   DEFAULT 'false' COMMENT '是否上架',
  `size`        VARCHAR(16)  DEFAULT NULL COMMENT '接口并发上限',
  `str1`        VARCHAR(255) DEFAULT NULL,
  `str2`        VARCHAR(255) DEFAULT NULL,
  `str3`        VARCHAR(255) DEFAULT NULL,
  `str4`        VARCHAR(255) DEFAULT NULL,
  `str5`        VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_customer_tran_no` (`tranno`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='渠道接口定义';

-- 3. 渠道请求 Tag
CREATE TABLE IF NOT EXISTS `esb_customer_request_tag` (
  `id`             INT NOT NULL AUTO_INCREMENT,
  `customertranno` VARCHAR(64)  DEFAULT NULL COMMENT '所属交易码',
  `tagname`        VARCHAR(128) DEFAULT NULL COMMENT '标签名',
  `parenttag`      VARCHAR(255) DEFAULT NULL COMMENT '父路径',
  `tagmapping`     VARCHAR(255) DEFAULT NULL COMMENT '取值路径（点分）',
  `tagtype`        VARCHAR(32)  DEFAULT NULL COMMENT 'String/Object/List/SizeList/NoSizeList',
  `tagvalue`       VARCHAR(255) DEFAULT NULL COMMENT '固定值',
  `sort`           INT          DEFAULT NULL COMMENT '同级排序',
  `tagflag`        VARCHAR(32)  DEFAULT NULL COMMENT '语义标记',
  `taglength`      INT          DEFAULT NULL COMMENT '定长长度',
  `str1`           VARCHAR(255) DEFAULT NULL,
  `str2`           VARCHAR(255) DEFAULT NULL,
  `str3`           VARCHAR(255) DEFAULT NULL,
  `str4`           VARCHAR(255) DEFAULT NULL,
  `str5`           VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_cust_req_tran` (`customertranno`),
  KEY `idx_cust_req_path` (`customertranno`,`parenttag`,`tagname`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='渠道请求 Tag';

-- 4. 渠道响应 Tag
CREATE TABLE IF NOT EXISTS `esb_customer_response_tag` (
  `id`             INT NOT NULL AUTO_INCREMENT,
  `customertranno` VARCHAR(64)  DEFAULT NULL,
  `tagname`        VARCHAR(128) DEFAULT NULL,
  `parenttag`      VARCHAR(255) DEFAULT NULL,
  `tagmapping`     VARCHAR(255) DEFAULT NULL,
  `tagtype`        VARCHAR(32)  DEFAULT NULL,
  `tagvalue`       VARCHAR(255) DEFAULT NULL,
  `sort`           INT          DEFAULT NULL,
  `tagflag`        VARCHAR(32)  DEFAULT NULL,
  `taglength`      INT          DEFAULT NULL,
  `str1`           VARCHAR(255) DEFAULT NULL,
  `str2`           VARCHAR(255) DEFAULT NULL,
  `str3`           VARCHAR(255) DEFAULT NULL,
  `str4`           VARCHAR(255) DEFAULT NULL,
  `str5`           VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_cust_res_tran` (`customertranno`),
  KEY `idx_cust_res_path` (`customertranno`,`parenttag`,`tagname`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='渠道响应 Tag';

-- 5. 数据源接口（第三方系统）
CREATE TABLE IF NOT EXISTS `esb_provider_transaction_info` (
  `id`              INT NOT NULL AUTO_INCREMENT,
  `providertranno`  VARCHAR(64)  DEFAULT NULL COMMENT '目标接口编号',
  `customertranno`  VARCHAR(64)  DEFAULT NULL COMMENT '关联的渠道交易码',
  `datatype`        VARCHAR(16)  DEFAULT NULL COMMENT 'json/xml/dc',
  `sendurl`         VARCHAR(512) DEFAULT NULL COMMENT '发送地址',
  `sysname`         VARCHAR(128) DEFAULT NULL COMMENT '目标系统名称',
  `codeType`        VARCHAR(32)  DEFAULT NULL COMMENT '编码，统一 UTF-8',
  `str1`            VARCHAR(255) DEFAULT NULL,
  `str2`            VARCHAR(255) DEFAULT NULL,
  `str3`            VARCHAR(255) DEFAULT NULL,
  `str4`            VARCHAR(255) DEFAULT NULL,
  `str5`            VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_provider_tran_no` (`providertranno`),
  KEY `idx_provider_customer_tran` (`customertranno`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='数据源接口定义';

-- 6. 数据源请求 Tag
CREATE TABLE IF NOT EXISTS `esb_provider_request_tag` (
  `id`             INT NOT NULL AUTO_INCREMENT,
  `providertranno` VARCHAR(64)  DEFAULT NULL,
  `tagname`        VARCHAR(128) DEFAULT NULL,
  `parenttag`      VARCHAR(255) DEFAULT NULL,
  `tagmapping`     VARCHAR(255) DEFAULT NULL,
  `tagtype`        VARCHAR(32)  DEFAULT NULL,
  `tagvalue`       VARCHAR(255) DEFAULT NULL,
  `sort`           INT          DEFAULT NULL,
  `tagflag`        VARCHAR(32)  DEFAULT NULL,
  `taglength`      INT          DEFAULT NULL,
  `str1`           VARCHAR(255) DEFAULT NULL,
  `str2`           VARCHAR(255) DEFAULT NULL,
  `str3`           VARCHAR(255) DEFAULT NULL,
  `str4`           VARCHAR(255) DEFAULT NULL,
  `str5`           VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_pro_req_tran` (`providertranno`),
  KEY `idx_pro_req_path` (`providertranno`,`parenttag`,`tagname`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='数据源请求 Tag';

-- 7. 数据源响应 Tag
CREATE TABLE IF NOT EXISTS `esb_provider_response_tag` (
  `id`             INT NOT NULL AUTO_INCREMENT,
  `providertranno` VARCHAR(64)  DEFAULT NULL,
  `tagname`        VARCHAR(128) DEFAULT NULL,
  `parenttag`      VARCHAR(255) DEFAULT NULL,
  `tagmapping`     VARCHAR(255) DEFAULT NULL,
  `tagtype`        VARCHAR(32)  DEFAULT NULL,
  `tagvalue`       VARCHAR(255) DEFAULT NULL,
  `sort`           INT          DEFAULT NULL,
  `tagflag`        VARCHAR(32)  DEFAULT NULL,
  `taglength`      INT          DEFAULT NULL,
  `str1`           VARCHAR(255) DEFAULT NULL,
  `str2`           VARCHAR(255) DEFAULT NULL,
  `str3`           VARCHAR(255) DEFAULT NULL,
  `str4`           VARCHAR(255) DEFAULT NULL,
  `str5`           VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_pro_res_tran` (`providertranno`),
  KEY `idx_pro_res_path` (`providertranno`,`parenttag`,`tagname`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='数据源响应 Tag';

-- 8. 交易日志
CREATE TABLE IF NOT EXISTS `esb_tran_log` (
  `id`         INT NOT NULL AUTO_INCREMENT,
  `nonce`      VARCHAR(64) DEFAULT NULL COMMENT '流水号',
  `trandate`   VARCHAR(8)  DEFAULT NULL COMMENT '交易日期',
  `reqchannel` VARCHAR(64) DEFAULT NULL COMMENT '请求渠道',
  `trancode`   VARCHAR(64) DEFAULT NULL COMMENT '交易码',
  `startdate`  VARCHAR(17) DEFAULT NULL COMMENT '开始时间',
  `enddate`    VARCHAR(17) DEFAULT NULL COMMENT '结束时间',
  `res`        VARCHAR(32) DEFAULT NULL COMMENT '响应码',
  `tranflag`   VARCHAR(64) DEFAULT NULL COMMENT 'serid 链路号',
  `logflag`    VARCHAR(32) DEFAULT NULL COMMENT 'OK/ERR',
  `str1`       VARCHAR(255) DEFAULT NULL,
  `str2`       VARCHAR(255) DEFAULT NULL,
  `str3`       VARCHAR(255) DEFAULT NULL,
  `str4`       VARCHAR(255) DEFAULT NULL,
  `str5`       VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_tranlog_flag` (`tranflag`),
  KEY `idx_tranlog_date` (`trandate`),
  KEY `idx_tranlog_code` (`trancode`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='交易流水日志';


-- =============== 02-admin-tables.sql ===============
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


-- =============== 03-init-data.sql ===============
-- ============================================================
-- 初始化数据：角色、权限、日志开关、错误码字典
-- 可重复执行（INSERT IGNORE + 唯一键）
-- ============================================================

USE `esb_platform`;

-- ---------- 角色 ----------
INSERT IGNORE INTO `esb_admin_role` (`id`,`role_code`,`role_name`,`remark`) VALUES
 (1,'ADMIN','系统管理员','全部权限'),
 (2,'CONFIG','配置员','配置与草稿'),
 (3,'APPROVER','审批员','审批与发布'),
 (4,'OPERATOR','运维','日志与监控'),
 (5,'READONLY','只读用户','仅查看');

-- ---------- 权限点 ----------
INSERT IGNORE INTO `esb_admin_permission` (`id`,`parent_id`,`perm_code`,`perm_name`,`type`,`path`,`sort`) VALUES
 (1,0,'menu:dashboard','工作台','menu','/dashboard',1),
 (10,0,'group:config','配置中心','menu','',10),
 (11,10,'menu:channel','渠道系统','menu','/config/channels',1),
 (12,10,'menu:customerTran','渠道接口','menu','/config/customer-transactions',2),
 (13,10,'menu:providerTran','数据源接口','menu','/config/provider-transactions',3),
 (14,10,'menu:tag','Tag 配置','menu','/config/tags',4),
 (15,10,'menu:mapping','字段映射','menu','/config/mapping',5),
 (16,10,'menu:sample','报文样例','menu','/config/samples',6),
 (17,10,'menu:version','发布管理','menu','/config/versions',7),
 (20,0,'group:monitor','运行监控','menu','',20),
 (21,20,'menu:log','交易日志','menu','/monitor/logs',1),
 (22,20,'menu:debug','在线调试','menu','/monitor/debug',2),
 (23,20,'menu:cache','缓存状态','menu','/monitor/cache',3),
 (30,0,'group:system','系统管理','menu','',30),
 (31,30,'menu:errorCode','错误码字典','menu','/system/error-codes',1),
 (32,30,'menu:key','密钥管理','menu','/system/keys',2),
 (33,30,'menu:cport','TCP 端口映射','menu','/system/cport',3),
 (34,30,'menu:logSwitch','日志开关','menu','/system/log-switches',4),
 (35,30,'menu:user','用户管理','menu','/system/users',5),
 (36,30,'menu:role','角色权限','menu','/system/roles',6),
 (37,30,'menu:audit','操作审计','menu','/system/audit',7),
 (40,0,'menu:simulator','接口模拟器','menu','/tools/simulator',40),
 (100,14,'btn:tag:save','保存 Tag','button','',1),
 (101,17,'btn:version:publish','发布配置','button','',1),
 (102,17,'btn:version:rollback','回滚配置','button','',2);

-- ---------- 角色-权限 ----------
-- 管理员拥有全部
INSERT IGNORE INTO `esb_admin_role_permission` (`role_id`,`permission_id`)
SELECT r.`id`, p.`id` FROM `esb_admin_role` r CROSS JOIN `esb_admin_permission` p
WHERE r.`role_code` = 'ADMIN';

-- 配置员：配置中心 + 报文样例 + 按钮
INSERT IGNORE INTO `esb_admin_role_permission` (`role_id`,`permission_id`)
SELECT r.`id`, p.`id` FROM `esb_admin_role` r CROSS JOIN `esb_admin_permission` p
WHERE r.`role_code` = 'CONFIG' AND p.`perm_code` IN
 ('menu:channel','menu:customerTran','menu:providerTran','menu:tag','menu:mapping','menu:sample','menu:version','btn:tag:save');

-- 审批员：只看配置 + 发布
INSERT IGNORE INTO `esb_admin_role_permission` (`role_id`,`permission_id`)
SELECT r.`id`, p.`id` FROM `esb_admin_role` r CROSS JOIN `esb_admin_permission` p
WHERE r.`role_code` = 'APPROVER' AND p.`perm_code` IN
 ('menu:tag','menu:mapping','menu:version','btn:version:publish','btn:version:rollback');

-- 运维：运行监控 + 日志开关 + 缓存
INSERT IGNORE INTO `esb_admin_role_permission` (`role_id`,`permission_id`)
SELECT r.`id`, p.`id` FROM `esb_admin_role` r CROSS JOIN `esb_admin_permission` p
WHERE r.`role_code` = 'OPERATOR' AND p.`perm_code` IN
 ('menu:log','menu:debug','menu:cache','menu:logSwitch');

-- ---------- 日志开关（对应 logConfig.json 的 01~08） ----------
INSERT IGNORE INTO `esb_log_switch` (`log_type`,`enabled`,`remark`) VALUES
 ('01','OK','渠道请求'),
 ('02','OK','返回渠道'),
 ('03','OK','第三方请求'),
 ('04','OK','第三方返回'),
 ('05','OFF','转换主程序'),
 ('06','OFF','转换辅助'),
 ('07','OK','耗时'),
 ('08','OFF','解析标签');

-- ---------- 错误码字典（依据 docs/05 现状整理） ----------
INSERT IGNORE INTO `esb_error_code` (`code`,`message`,`module`,`suggestion`,`status`) VALUES
 ('9433','已达到渠道请求并发量','accept-http','调大渠道并发或稍后重试','ENABLE'),
 ('2132','已达到接口请求并发量','accept-http','调大接口并发或稍后重试','ENABLE'),
 ('0999','禁止访问，渠道未启用','accept-http','检查渠道 flag','ENABLE'),
 ('98935','接口未上架','accept-http','在渠道接口中上架','ENABLE'),
 ('9895','接口未上架','accept-http','检查 isOpen 配置','ENABLE'),
 ('863','唯一流水号重复','accept-http','检查 nonce 生成','ENABLE'),
 ('9998','时间戳为空或位数不对','accept-http','当前代码未启用该校验','DISABLE'),
 ('9997','时间戳格式不正确','accept-http','检查 18 位时间戳','ENABLE'),
 ('9996','时间戳失效（早于当前时间）','accept-http','同步客户端时间','ENABLE'),
 ('9995','时间戳失效（晚于当前时间）','accept-http','同步客户端时间','ENABLE'),
 ('9991','PROCESS 系统未启动 / 交接编号不能为空','accept-http/process','同码不同义，待整改','ENABLE'),
 ('9999','兜底异常 / 非允许渠道','accept-http','同码不同义，待整改','ENABLE'),
 ('3453','process 端兜底异常','process','检查 process 日志','ENABLE'),
 ('092','缺少返回码标识','process','配置 tagflag=code/message','ENABLE'),
 ('06987','报文转化错误','process','检查四类 Tag 配置','ENABLE'),
 ('87342','解析有误，请检查上送报文格式','process/MapConvert','检查报文与 Tag 映射','ENABLE'),
 ('873242','解析有误，请检查上送报文格式','process/MapUtil','检查 tagMapping 路径','ENABLE'),
 ('4675434','报文上送格式有误','process/StringMapServe','检查报文格式','ENABLE'),
 ('4653434','报文上送格式有误','process/StringMapServe','检查输出配置','ENABLE'),
 ('6s532','定长报文解析错误','process/DC*Util','非数字错误码，待整改','DISABLE'),
 ('6532','定长报文解析错误','process/DC*Util','检查 taglength 与 sort','ENABLE'),
 ('865','转换失败 / 出站失败','process/OKHTTPUtil','检查目标地址与网络','ENABLE'),
 ('8001','连接异常，请确认端口','send-tcp','检查 cPort 配置','ENABLE'),
 ('8004','连接拒绝，请确认对方服务是否启动','send-tcp','检查目标服务','ENABLE'),
 ('8002','连接异常，IO 异常','send-tcp','检查网络','ENABLE'),
 ('8003','连接异常','send-tcp','查看堆栈','ENABLE');


