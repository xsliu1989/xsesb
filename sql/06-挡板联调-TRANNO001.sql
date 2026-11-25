-- ============================================================
-- 渠道模拟 → ESB → 后台挡板 联调配置
-- 挡板：http://localhost:8888  （moco-runner，Test.json）
-- 交易：TRANNO001 → P001
-- 响应：GET/POST /TRANNO001 返回 XML
--
-- 前提：已执行 00~03 建表脚本；若为空库，请先执行 04-demo-data.sql
-- 注意：ESB 元数据只在启动时加载，执行后需重启 accept-http 与 process
-- ============================================================

USE `esb_platform`;

-- 1. 渠道：只允许 CH01，避免 isChannel 过滤器用逗号串查缓存导致 NPE
--    注意：systype=渠道编号（缓存 key），sysname=渠道中文名称
UPDATE `esb_customer_sysname`
   SET `flag` = 'true', `size` = '100', `str1` = '模拟渠道CH01'
 WHERE `systype` = 'CH01';

-- 2. 渠道接口：上架、关闭加解密（否则 Decryption 过滤器会因无 SM4 Key 失败）
UPDATE `esb_customer_transaction_info`
   SET `sysname` = 'CH01',
       `clazz` = 'defaultService',
       `iscryption` = 'false',
       `isOpen` = 'true',
       `size` = '100'
 WHERE `tranno` = 'TRANNO001';

-- 3. 数据源接口：指向挡板服务；挡板返回 XML，故 datatype 设为 xml
UPDATE `esb_provider_transaction_info`
   SET `providertranno` = 'P001',
       `customertranno` = 'TRANNO001',
       `datatype` = 'xml',
       `sendurl` = 'http://localhost:8888/TRANNO001',
       `sysname` = '后台挡板服务',
       `codeType` = 'UTF-8'
 WHERE `providertranno` = 'P001';

-- 4. 渠道请求 Tag：解析渠道上送的 data（JSON）
DELETE FROM `esb_customer_request_tag` WHERE `customertranno` = 'TRANNO001';
INSERT INTO `esb_customer_request_tag`
 (`customertranno`,`tagname`,`parenttag`,`tagmapping`,`tagtype`,`tagvalue`,`sort`,`tagflag`,`taglength`) VALUES
 ('TRANNO001','transaction','TRANNO001','transaction','Object','',1,'TOP',NULL),
 ('TRANNO001','header','TRANNO001.transaction','transaction.header','Object','',1,'',NULL),
 ('TRANNO001','msg','TRANNO001.transaction.header','transaction.header.msg','Object','',1,'',NULL),
 ('TRANNO001','seqNb','TRANNO001.transaction.header.msg','transaction.header.msg.seqNb','String','',1,'nonce',NULL),
 ('TRANNO001','sndDt','TRANNO001.transaction.header.msg','transaction.header.msg.sndDt','String','',2,'',NULL),
 ('TRANNO001','body','TRANNO001.transaction','transaction.body','Object','',2,'',NULL),
 ('TRANNO001','request','TRANNO001.transaction.body','transaction.body.request','Object','',1,'',NULL),
 ('TRANNO001','orderId','TRANNO001.transaction.body.request','transaction.body.request.orderId','String','',1,'',NULL);

-- 5. 数据源请求 Tag：生成发给挡板的 XML 请求（挡板不校验报文体）
DELETE FROM `esb_provider_request_tag` WHERE `providertranno` = 'P001';
INSERT INTO `esb_provider_request_tag`
 (`providertranno`,`tagname`,`parenttag`,`tagmapping`,`tagtype`,`tagvalue`,`sort`,`tagflag`,`taglength`) VALUES
 ('P001','transaction','P001','transaction','Object','',1,'TOP',NULL),
 ('P001','header','P001.transaction','transaction.header','Object','',1,'',NULL),
 ('P001','msg','P001.transaction.header','transaction.header.msg','Object','',1,'',NULL),
 ('P001','seqNb','P001.transaction.header.msg','transaction.header.msg.seqNb','String','',1,'',NULL),
 ('P001','body','P001.transaction','transaction.body','Object','',2,'',NULL),
 ('P001','request','P001.transaction.body','transaction.body.request','Object','',1,'',NULL),
 ('P001','orderId','P001.transaction.body.request','transaction.body.request.orderId','String','',1,'',NULL);

-- 6. 数据源响应 Tag：从挡板 XML 响应中取 retCd / desc
--    挡板响应结构：transaction.header.status.retCd / transaction.header.status.desc
DELETE FROM `esb_provider_response_tag` WHERE `providertranno` = 'P001';
INSERT INTO `esb_provider_response_tag`
 (`providertranno`,`tagname`,`parenttag`,`tagmapping`,`tagtype`,`tagvalue`,`sort`,`tagflag`,`taglength`) VALUES
 ('P001','response','P001','transaction','Object','',1,'TOP',NULL),
 ('P001','retCd','P001.response','transaction.header.status.retCd','String','',1,'code',NULL),
 ('P001','desc','P001.response','transaction.header.status.desc','String','',2,'message',NULL);

-- 7. 渠道响应 Tag：把 provider_res_map 的 response.retCd / response.desc 转成渠道响应
DELETE FROM `esb_customer_response_tag` WHERE `customertranno` = 'TRANNO001';
INSERT INTO `esb_customer_response_tag`
 (`customertranno`,`tagname`,`parenttag`,`tagmapping`,`tagtype`,`tagvalue`,`sort`,`tagflag`,`taglength`) VALUES
 ('TRANNO001','response','TRANNO001','response','Object','',1,'TOP',NULL),
 ('TRANNO001','returnCode','TRANNO001.response','response.retCd','String','',1,'code',NULL),
 ('TRANNO001','returnMess','TRANNO001.response','response.desc','String','',2,'message',NULL);

-- 8. 校验
SELECT t.`tranno`, t.`sysname`, t.`iscryption`, t.`isOpen`, p.`datatype`, p.`sendurl`
  FROM `esb_customer_transaction_info` t
  JOIN `esb_provider_transaction_info` p ON p.`customertranno` = t.`tranno`
 WHERE t.`tranno` = 'TRANNO001';
