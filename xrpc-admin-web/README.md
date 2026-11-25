# xrpc-admin-web

ESB 前台管理控制台前端（Vue 3 + TypeScript + Element Plus + Vite）。

## 运行

环境需 Node.js 18+：

```bash
npm install
npm run dev      # http://localhost:9520
npm run build    # 输出到 dist/
```

## 演示模式

`.env.development` 中 `VITE_USE_MOCK=true`，所有 `/admin-api` 请求由 `src/mock/mockAdapter.ts` 拦截，数据保存在 localStorage，无需启动后端即可完整演示。登录页任意账号密码均可登录。

切真实后端：把 `VITE_USE_MOCK` 改为 `false` 并**重启 dev server**，同时启动 `xrpc-admin`（9521）。前端固定使用 9520，不要让它自动跳到 9521，否则 `/admin-api` 代理会指向自己。此时模拟器发出的请求才会真正进入你的 ESB 项目（accept-http:9300），IDEA 控制台/日志里才会有请求记录。

## 目录

- `src/api/`：统一请求封装与各资源 API
- `src/mock/`：Mock 适配器与种子数据
- `src/router/`、`src/store/`：路由与 Pinia 状态
- `src/layout/`：整体布局、菜单与方案 A 提示
- `src/components/`：通用 CRUD 页、Tag 树编辑器
- `src/views/`：各功能页面
- `src/utils/tagTree.ts`：Tag 扁平/树互转、校验、样例推断（核心算法）

## 核心页面

- `Tag 配置`：四类 Tag 树形编辑 + 校验 + 样例取值预览 + 保存草稿
- `字段映射`：渠道入参与目标入参按 `tagMapping` 对照
- `发布管理`：草稿 → 审批 → 发布 → 回滚，发布后提示「需重启 ESB 生效」
- `在线调试`：四段式转换阶段快照
- `交易日志`：按 serid 查询并展示阶段报文
