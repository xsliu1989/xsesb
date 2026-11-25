import type { TagNode } from '@/utils/tagTree'

type Row = Record<string, any>

const KEY = 'xrpc-admin-mock-db-v1'

function tag(id: number, ownerType: string, ownerKey: string, tagName: string, parentTag: string, tagMapping: string, tagType: string, sort: number, extra: Partial<TagNode> = {}): Row {
  return { id, ownerType, ownerKey, tagName, parentTag, tagMapping, tagType, sort, tagValue: '', tagflag: '', taglength: null, ...extra }
}

function seed(): any {
  const channels: Row[] = [
    { id: 1, systype: 'CHANNEL', sysname: 'CH01', flag: 'true', size: '100', str1: '渠道A', str2: '', str3: '', str4: '', str5: '' },
    { id: 2, systype: 'CHANNEL', sysname: 'CH02', flag: 'true', size: '50', str1: '渠道B', str2: '', str3: '', str4: '', str5: '' },
    { id: 3, systype: 'CHANNEL', sysname: 'CH03', flag: 'false', size: '20', str1: '渠道C（停用）', str2: '', str3: '', str4: '', str5: '' }
  ]
  const customerTransactions: Row[] = [
    { id: 1, tranNo: 'TRANNO001', sysName: 'CH01,CH02', clazz: 'defaultService', isCryption: 'true', isOpen: 'true', size: '100' },
    { id: 2, tranNo: 'TRANNO002', sysName: 'CH01', clazz: 'defaultService', isCryption: 'false', isOpen: 'true', size: '50' },
    { id: 3, tranNo: 'TRANNO003', sysName: 'CH02', clazz: 'defaultService', isCryption: 'false', isOpen: 'false', size: '30' }
  ]
  const providerTransactions: Row[] = [
    { id: 1, providerTranNo: 'P001', customerTranNo: 'TRANNO001', dataType: 'json', sendUrl: 'http://127.0.0.1:8080/api/trade', sysName: 'C系统', codeType: 'UTF-8' },
    { id: 2, providerTranNo: 'P002', customerTranNo: 'TRANNO002', dataType: 'xml', sendUrl: 'http://127.0.0.1:8081/api/trade', sysName: 'D系统', codeType: 'GBK' },
    { id: 3, providerTranNo: 'P003', customerTranNo: 'TRANNO003', dataType: 'dc', sendUrl: 'tcp://127.0.0.1:8888', sysName: 'E系统', codeType: 'GBK' }
  ]
  const tags: Row[] = [
    // 渠道请求
    tag(1, 'CUSTOMER_REQUEST', 'TRANNO001', 'transaction', 'TRANNO001', 'transaction', 'Object', 1, { tagflag: 'TOP' }),
    tag(2, 'CUSTOMER_REQUEST', 'TRANNO001', 'header', 'TRANNO001.transaction', 'transaction.header', 'Object', 1),
    tag(3, 'CUSTOMER_REQUEST', 'TRANNO001', 'msg', 'TRANNO001.transaction.header', 'transaction.header.msg', 'Object', 1),
    tag(4, 'CUSTOMER_REQUEST', 'TRANNO001', 'seqNb', 'TRANNO001.transaction.header.msg', 'transaction.header.msg.seqNb', 'String', 1, { tagflag: 'nonce' }),
    tag(5, 'CUSTOMER_REQUEST', 'TRANNO001', 'sndDt', 'TRANNO001.transaction.header.msg', 'transaction.header.msg.sndDt', 'String', 2),
    tag(6, 'CUSTOMER_REQUEST', 'TRANNO001', 'body', 'TRANNO001.transaction', 'transaction.body', 'Object', 2),
    tag(7, 'CUSTOMER_REQUEST', 'TRANNO001', 'request', 'TRANNO001.transaction.body', 'transaction.body.request', 'Object', 1),
    tag(8, 'CUSTOMER_REQUEST', 'TRANNO001', 'orderId', 'TRANNO001.transaction.body.request', 'transaction.body.request.orderId', 'String', 1),
    // 渠道返回
    tag(9, 'CUSTOMER_RESPONSE', 'TRANNO001', 'response', 'TRANNO001', 'response', 'Object', 1, { tagflag: 'TOP' }),
    tag(10, 'CUSTOMER_RESPONSE', 'TRANNO001', 'returnCode', 'TRANNO001.response', 'response.returnCode', 'String', 1, { tagflag: 'code' }),
    tag(11, 'CUSTOMER_RESPONSE', 'TRANNO001', 'returnMess', 'TRANNO001.response', 'response.returnMess', 'String', 2, { tagflag: 'message' }),
    // 数据源请求（后台按 providertranno 存储，此处为 P001）
    tag(12, 'PROVIDER_REQUEST', 'P001', 'transaction', 'P001', 'transaction', 'Object', 1, { tagflag: 'TOP' }),
    tag(13, 'PROVIDER_REQUEST', 'P001', 'header', 'P001.transaction', 'transaction.header', 'Object', 1),
    tag(14, 'PROVIDER_REQUEST', 'P001', 'msg', 'P001.transaction.header', 'transaction.header.msg', 'Object', 1),
    tag(15, 'PROVIDER_REQUEST', 'P001', 'seqNb', 'P001.transaction.header.msg', 'transaction.header.msg.seqNb', 'String', 1),
    tag(16, 'PROVIDER_REQUEST', 'P001', 'body', 'P001.transaction', 'transaction.body', 'Object', 2),
    tag(17, 'PROVIDER_REQUEST', 'P001', 'request', 'P001.transaction.body', 'transaction.body.request', 'Object', 1),
    tag(18, 'PROVIDER_REQUEST', 'P001', 'orderId', 'P001.transaction.body.request', 'transaction.body.request.orderId', 'String', 1),
    // 数据源返回
    tag(19, 'PROVIDER_RESPONSE', 'P001', 'response', 'P001', 'response', 'Object', 1, { tagflag: 'TOP' }),
    tag(20, 'PROVIDER_RESPONSE', 'P001', 'retCd', 'P001.response', 'response.retCd', 'String', 1, { tagflag: 'code' }),
    tag(21, 'PROVIDER_RESPONSE', 'P001', 'retMsg', 'P001.response', 'response.retMsg', 'String', 2, { tagflag: 'message' })
  ]
  const samples: Row[] = [
    { id: 1, tranNo: 'TRANNO001', sampleType: 'REQUEST', content: '{"transaction":{"header":{"msg":{"seqNb":"111106500000497","sndDt":"20220113"}},"body":{"request":{"orderId":"O123"}}}}', remark: '标准下单请求' },
    { id: 2, tranNo: 'TRANNO001', sampleType: 'RESPONSE', content: '{"response":{"returnCode":"000000","returnMess":"成功"}}', remark: '成功响应' }
  ]
  const errorCodes: Row[] = [
    { id: 1, code: '9433', message: '已达到渠道请求并发量', module: 'accept-http', suggestion: '调大渠道并发或稍后重试' },
    { id: 2, code: '2132', message: '已达到接口请求并发量', module: 'accept-http', suggestion: '调大接口并发或稍后重试' },
    { id: 3, code: '98935', message: '接口未上架！', module: 'accept-http', suggestion: '在渠道接口中上架' },
    { id: 4, code: '863', message: '唯一流水号重复', module: 'accept-http', suggestion: '检查 nonce 生成逻辑' },
    { id: 5, code: '06987', message: '报文转化错误', module: 'process', suggestion: '检查四类 Tag 配置' }
  ]
  const cport: Row[] = [
    { id: 1, trannos: 'TRANNO003', address: '127.0.0.1', port: 8888, charset: 'GBK', fixedlength: 6, type: 'DC' }
  ]
  const keys: Row[] = [
    { id: 1, channel: 'CH01', keyType: 'SM2', fingerprint: 'A1:B2:C3:D4:E5:F6', cipherRef: 'kms://sm2/CH01', status: 'ENABLE', expireTime: '2027-01-01' },
    { id: 2, channel: 'CH02', keyType: 'SM4', fingerprint: '11:22:33:44:55:66', cipherRef: 'kms://sm4/CH02', status: 'ENABLE', expireTime: '2027-06-01' }
  ]
  const users: Row[] = [
    { id: 1, username: 'admin', realName: '系统管理员', status: 'ENABLE', roles: 'ADMIN' },
    { id: 2, username: 'cfg', realName: '配置员', status: 'ENABLE', roles: 'CONFIG' },
    { id: 3, username: 'auditor', realName: '审批员', status: 'ENABLE', roles: 'APPROVER' }
  ]
  const roles: Row[] = [
    { id: 1, roleCode: 'ADMIN', roleName: '系统管理员', remark: '全部权限' },
    { id: 2, roleCode: 'CONFIG', roleName: '配置员', remark: '配置与草稿' },
    { id: 3, roleCode: 'APPROVER', roleName: '审批员', remark: '审批与发布' },
    { id: 4, roleCode: 'OPERATOR', roleName: '运维', remark: '日志与监控' },
    { id: 5, roleCode: 'READONLY', roleName: '只读用户', remark: '仅查看' }
  ]
  const logSwitches: Row[] = [
    { logType: '01', enabled: 'OK', remark: '渠道请求' }, { logType: '02', enabled: 'OK', remark: '返回渠道' },
    { logType: '03', enabled: 'OK', remark: '第三方请求' }, { logType: '04', enabled: 'OK', remark: '第三方返回' },
    { logType: '05', enabled: 'OFF', remark: '转换主程序' }, { logType: '06', enabled: 'OFF', remark: '转换辅助' },
    { logType: '07', enabled: 'OK', remark: '耗时' }, { logType: '08', enabled: 'OFF', remark: '解析标签' }
  ]
  const versions: Row[] = []
  const auditLogs: Row[] = []
  const tranLogs: Row[] = [
    { id: 1, nonce: '20220113111106500001', trandate: '20220113', reqchannel: 'CH01', trancode: 'TRANNO001', startdate: '20220113111106000', enddate: '20220113111106120', res: '0000', tranflag: 'serid-demo-0001', logflag: 'OK' },
    { id: 2, nonce: '20220113111206500002', trandate: '20220113', reqchannel: 'CH02', trancode: 'TRANNO002', startdate: '20220113111206000', enddate: '20220113111206990', res: '06987', tranflag: 'serid-demo-0002', logflag: 'ERR' }
  ]
  return {
    channels, customerTransactions, providerTransactions, tags, samples, errorCodes,
    cport, keys, users, roles, logSwitches, versions, auditLogs, tranLogs
  }
}

export function loadDb(): any {
  try {
    const raw = localStorage.getItem(KEY)
    if (raw) return JSON.parse(raw)
  } catch { /* ignore */ }
  const db = seed()
  saveDb(db)
  return db
}

export function saveDb(db: any) {
  try { localStorage.setItem(KEY, JSON.stringify(db)) } catch { /* ignore */ }
}

export function resetDb() {
  localStorage.removeItem(KEY)
}
