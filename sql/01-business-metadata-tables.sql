-- ============================================================
-- 既有 ESB 后台的 8 张元数据 / 日志表
-- 依据 JPA 实体反推：com.xs.datamodel.* 与 com.xs.model.Tag
-- 若生产库已存在同名表，CREATE TABLE IF NOT EXISTS 不会覆盖
-- ============================================================

USE `esb_platform`;

-- 1. 渠道系统（请求发起方）
CREATE TABLE IF NOT EXISTS `esb_customer_sysname` (
  `id`      INT NOT NULL AUTO_INCREMENT,
  `systype` VARCHAR(64)  DEFAULT NULL COMMENT '系统类型',
  `sysname` VARCHAR(128) DEFAULT NULL COMMENT '渠道标识',
  `flag`    VARCHAR(8)   DEFAULT 'true' COMMENT '是否启用 true/false',
  `size`    VARCHAR(16)  DEFAULT NULL COMMENT '渠道最大并发',
  `str1`    VARCHAR(255) DEFAULT NULL,
  `str2`    VARCHAR(255) DEFAULT NULL,
  `str3`    VARCHAR(255) DEFAULT NULL,
  `str4`    VARCHAR(255) DEFAULT NULL,
  `str5`    VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_customer_sysname_name` (`sysname`)
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
  `codeType`        VARCHAR(32)  DEFAULT NULL COMMENT '编码 UTF-8/GBK',
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
