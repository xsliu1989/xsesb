# xrpc-admin

ESB 前台管理控制台后端。**独立工程**，不注册进既有 Maven reactor，因此不会修改现有 ESB 后台的构建与运行。

## 构建与运行

需 JDK 8 + Maven 3.6+。

由于复用 `com.xs.datamodel.*` 元数据实体，需先把公共模块安装到本地仓库：

```bash
cd F:\testesb
mvn install -pl xrpc-common/xrpc-common-serve,xrpc-common/xrpc-common-crud,xrpc-common/xrpc-common-redis,xrpc-common/xrpc-common-cache/xrpc-common-cache-datastructure -am -DskipTests
```

然后构建本模块：

```bash
cd F:\testesb\xrpc-admin
mvn spring-boot:run
```

默认端口 `9521`，API 前缀 `/admin-api`，Swagger：`http://localhost:9521/swagger-ui.html`。

> 已修复：`xrpc-common/pom.xml` 中错误的 `xrpc-module-esb-service` 模块声明已移除，`xrpc-common-cache` 的 parent 已改为 `xrpc-common`，上述 `mvn install` 可正常执行。

## 配置

| 环境变量 | 默认值 | 说明 |
|---|---|---|
| `SPRING_DATASOURCE_URL` | `jdbc:mysql://192.168.18.131:3306/esb_platform...` | 元数据库地址（已切换新库） |
| `SPRING_DATASOURCE_USERNAME/PASSWORD` | root/root | 数据库账号 |
| `ADMIN_JWT_SECRET` | 演示值 | JWT 密钥，生产必须替换 |
| `ESB_REFRESH_ENDPOINTS` | 空 | 方案 B 下 ESB 实例刷新端点，逗号分隔 |
| `ESB_EFFECT_MODE` | `RESTART_REQUIRED` | 配置生效方式 |

## 数据库

- 8 张业务元数据表沿用现有库（见 `docs/03-详细设计.md` 第 10 章）；
- 11 张管理表 DDL 见 `docs/08-前台管理系统-详细设计.md` 第 5 章；
- `spring.jpa.hibernate.ddl-auto=update`，首次启动会自动建表；
- `DataInitializer` 自动初始化 5 个内置角色、管理员账号 `admin/admin123` 与 8 类日志开关。

## 代码结构

```
com.xs.admin
├─ common/       统一响应 R、分页、错误码、全局异常处理
├─ config/       Security、OpenAPI、初始化数据
├─ security/     JWT 工具与过滤器
├─ aop/          操作审计注解与切面
├─ entity/       管理表实体 + esb_tran_log 只读视图
├─ dto/          TagDTO、VersionDraftDTO
├─ repository/   GenericRepository（管理表与元数据表共用）
├─ service/      MetaService / TagService / VersionService / AuthService
└─ controller/   AuthController / ConfigController / SystemController
```

## 方案 A / B

- 当前为**方案 A**：发布写业务表，但 ESB 进程不会立即感知，需重启或等待既有加载时机；
- 升级方案 B：配置 `ESB_REFRESH_ENDPOINTS`，并在 ESB 侧新增 `POST /internal/refresh`（契约见 `docs/08` 第 4.5 节）。前端与 API 契约不变。
