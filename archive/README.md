# 归档模块说明

## xrpc-module-esb-service（归档于 2026-11-25）

### 归档原因

该模块是 `xrpc-module-process` 的历史重复实现（拆分前的"接入+处理"单体版本），当前状态：

- 不在 `xrpc-module/pom.xml` 的 `<modules>` 中，不参与 reactor 构建
- 无任何模块依赖它的 artifact
- 无编译产物（`target` 目录不存在），从未被构建或运行
- 项目文档 `docs/02-概要设计.md`、`docs/04-现状差异与改造建议.md`（P0-01/P0-02）已明确将其定性为"历史重复实现，建议归档"

实际生效的服务是 `accept-http`（接入/过滤）+ `process`（转换/转发）两个模块。

### 归档前已迁移的差异点

`xrpc-module-esb-service` 相比 `process` 唯一有价值的差异，是 `DefaultAbstractServiceRedis` 中的 `filter(List<Tag>)` 方法：从请求 Tag 中把 `nonce/timestamp/channel/digestAlgorithm` 回填到 Dto。

已迁移到：

```
xrpc-module-process/src/main/java/com/xs/service/DefaultAbstractService.java
```

- 增加 `filter(List<Tag>)` 方法（照搬原实现）
- 在 `customer_req_map` 生成后调用 `this.filter(customerRequestTags)`
- 补充 `com.xs.util.MapUtil` 的 import

已用 UTF-8 编码编译验证通过（`-Dfile.encoding=UTF-8 -Dproject.build.sourceEncoding=UTF-8`）。

### 安全评估

- 当前数据库 `esb_customer_request_tag` 只有 `tagflag=nonce` 的配置（TRANNO001 的 `seqNb`），没有 channel/timestamp/digestAlgorithm 类型的 Tag，因此 `filter()` 不会覆盖接入层设置的 `dto.channelType`（JSON/XML/DC），返回报文格式不受影响。
- `UpdateUrlDefalutService.update_customer_req_map()` 读取 `dto.nonce` 发生在 `filter()` 之前，不受 nonce 回填影响。

### 如需恢复

直接将本目录整体移回 `xrpc-module/xrpc-module-esb-service` 即可；其 pom 的 parent 已配置 `<relativePath>../xrpc-common/pom.xml</relativePath>`（移回后路径正确）。
