-- ============================================================
-- 演示数据（可选，仅空库 / 测试库执行）
-- 与前端 Mock 数据一致：TRANNO001 -> P001
-- ============================================================

USE `esb_platform`;

-- ---------- 渠道系统 ----------
-- systype=渠道编号（缓存 key，过滤器按此查询），sysname=渠道中文名称
INSERT IGNORE INTO `esb_customer_sysname` (`id`,`systype`,`sysname`,`flag`,`size`,`str1`) VALUES
 (1,'CH01','渠道A','true','100','模拟渠道CH01'),
 (2,'CH02','渠道B','true','50','渠道B'),
 (3,'CH03','渠道C（停用）','false','20','渠道C（停用）');

-- ---------- 渠道接口 ----------
INSERT IGNORE INTO `esb_customer_transaction_info`
 (`id`,`tranno`,`sysname`,`clazz`,`iscryption`,`isOpen`,`size`) VALUES
 (1,'TRANNO001','CH01,CH02','defaultService','true','true','100'),
 (2,'TRANNO002','CH01','defaultService','false','true','50'),
 (3,'TRANNO003','CH02','defaultService','false','false','30');

-- ---------- 数据源接口 ----------
INSERT IGNORE INTO `esb_provider_transaction_info`
 (`id`,`providertranno`,`customertranno`,`datatype`,`sendurl`,`sysname`,`codeType`) VALUES
 (1,'P001','TRANNO001','json','http://127.0.0.1:8080/api/trade','C系统','UTF-8'),
 (2,'P002','TRANNO002','xml','http://127.0.0.1:8081/api/trade','D系统','GBK'),
 (3,'P003','TRANNO003','dc','tcp://127.0.0.1:8888','E系统','GBK');

-- ---------- 渠道请求 Tag（归属键 = tranno） ----------
INSERT IGNORE INTO `esb_customer_request_tag`
 (`id`,`customertranno`,`tagname`,`parenttag`,`tagmapping`,`tagtype`,`tagvalue`,`sort`,`tagflag`,`taglength`) VALUES
 (1,'TRANNO001','transaction','TRANNO001','transaction','Object','',1,'TOP',NULL),
 (2,'TRANNO001','header','TRANNO001.transaction','transaction.header','Object','',1,'',NULL),
 (3,'TRANNO001','msg','TRANNO001.transaction.header','transaction.header.msg','Object','',1,'',NULL),
 (4,'TRANNO001','seqNb','TRANNO001.transaction.header.msg','transaction.header.msg.seqNb','String','',1,'nonce',NULL),
 (5,'TRANNO001','sndDt','TRANNO001.transaction.header.msg','transaction.header.msg.sndDt','String','',2,'',NULL),
 (6,'TRANNO001','body','TRANNO001.transaction','transaction.body','Object','',2,'',NULL),
 (7,'TRANNO001','request','TRANNO001.transaction.body','transaction.body.request','Object','',1,'',NULL),
 (8,'TRANNO001','orderId','TRANNO001.transaction.body.request','transaction.body.request.orderId','String','',1,'',NULL);

-- ---------- 渠道响应 Tag ----------
INSERT IGNORE INTO `esb_customer_response_tag`
 (`id`,`customertranno`,`tagname`,`parenttag`,`tagmapping`,`tagtype`,`tagvalue`,`sort`,`tagflag`,`taglength`) VALUES
 (9,'TRANNO001','response','TRANNO001','response','Object','',1,'TOP',NULL),
 (10,'TRANNO001','returnCode','TRANNO001.response','response.returnCode','String','',1,'code',NULL),
 (11,'TRANNO001','returnMess','TRANNO001.response','response.returnMess','String','',2,'message',NULL);

-- ---------- 数据源请求 Tag（归属键 = providertranno，即 P001） ----------
INSERT IGNORE INTO `esb_provider_request_tag`
 (`id`,`providertranno`,`tagname`,`parenttag`,`tagmapping`,`tagtype`,`tagvalue`,`sort`,`tagflag`,`taglength`) VALUES
 (12,'P001','transaction','P001','transaction','Object','',1,'TOP',NULL),
 (13,'P001','header','P001.transaction','transaction.header','Object','',1,'',NULL),
 (14,'P001','msg','P001.transaction.header','transaction.header.msg','Object','',1,'',NULL),
 (15,'P001','seqNb','P001.transaction.header.msg','transaction.header.msg.seqNb','String','',1,'',NULL),
 (16,'P001','body','P001.transaction','transaction.body','Object','',2,'',NULL),
 (17,'P001','request','P001.transaction.body','transaction.body.request','Object','',1,'',NULL),
 (18,'P001','orderId','P001.transaction.body.request','transaction.body.request.orderId','String','',1,'',NULL);

-- ---------- 数据源响应 Tag ----------
INSERT IGNORE INTO `esb_provider_response_tag`
 (`id`,`providertranno`,`tagname`,`parenttag`,`tagmapping`,`tagtype`,`tagvalue`,`sort`,`tagflag`,`taglength`) VALUES
 (19,'P001','response','P001','response','Object','',1,'TOP',NULL),
 (20,'P001','retCd','P001.response','response.retCd','String','',1,'code',NULL),
 (21,'P001','retMsg','P001.response','response.retMsg','String','',2,'message',NULL);

-- ---------- 黄金报文样例 ----------
INSERT IGNORE INTO `esb_msg_sample` (`id`,`tran_no`,`sample_type`,`content`,`remark`,`create_time`) VALUES
 (1,'TRANNO001','REQUEST','{"transaction":{"header":{"msg":{"seqNb":"111106500000497","sndDt":"20220113"}},"body":{"request":{"orderId":"O123"}}}}','标准下单请求',NOW()),
 (2,'TRANNO001','RESPONSE','{"response":{"returnCode":"000000","returnMess":"成功"}}','成功响应',NOW());

-- ---------- 密钥元信息 ----------
INSERT IGNORE INTO `esb_key_store` (`id`,`channel`,`key_type`,`fingerprint`,`cipher_ref`,`status`,`expire_time`,`create_time`) VALUES
 (1,'CH01','SM2','A1:B2:C3:D4:E5:F6','kms://sm2/CH01','ENABLE','2027-01-01',NOW()),
 (2,'CH02','SM4','11:22:33:44:55:66','kms://sm4/CH02','ENABLE','2027-06-01',NOW());

-- ---------- TCP 端口映射 ----------
INSERT IGNORE INTO `esb_cport_config`
 (`id`,`trannos`,`address`,`port`,`charset`,`fixedlength`,`type`) VALUES
 (1,'TRANNO003','127.0.0.1',8888,'GBK',6,'DC');

-- ---------- 交易日志 ----------
INSERT IGNORE INTO `esb_tran_log`
 (`id`,`nonce`,`trandate`,`reqchannel`,`trancode`,`startdate`,`enddate`,`res`,`tranflag`,`logflag`) VALUES
 (1,'20220113111106500001','20220113','CH01','TRANNO001','20220113111106000','20220113111106120','0000','serid-demo-0001','OK'),
 (2,'20220113111206500002','20220113','CH02','TRANNO002','20220113111206000','20220113111206990','06987','serid-demo-0002','ERR');
