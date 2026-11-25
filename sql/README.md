# 数据库脚本

本目录脚本面向 **MySQL 8.x**，UTF-8（utf8mb4）。按编号顺序执行即可。

| 文件 | 作用 | 是否必须 |
|---|---|---|
| `00-create-database.sql` | 创建数据库 | 是 |
| `01-business-metadata-tables.sql` | 既有 ESB 的 8 张元数据/日志表（仓库此前无 DDL，按 JPA 实体反推） | 是（若库里已有则用 `CREATE TABLE IF NOT EXISTS` 不会覆盖） |
| `02-admin-tables.sql` | 前台控制台的 12 张管理表 | 是 |
| `03-init-data.sql` | 角色、权限、日志开关、错误码字典 | 是 |
| `04-demo-data.sql` | 演示用的渠道、交易、Tag、日志数据 | 否（仅空库/测试库执行） |
| `06-挡板联调-TRANNO001.sql` | 把 TRANNO001 → P001 指到 `localhost:8888/TRANNO001`，并配置响应 Tag 提取 `retCd/desc` | 联调时执行 |
| `all-in-one.sql` | 00~03 的合并版，DBeaver 里一次执行即可 | 推荐 |
| `all-in-one-ascii.sql` | 合并版的纯 ASCII 精简版（去掉 COMMENT 子句），DBeaver 执行更稳 | 执行报错时用它 |

## 执行方式

```bash
mysql -u root -p < 00-create-database.sql
mysql -u root -p esb_platform < 01-business-metadata-tables.sql
mysql -u root -p esb_platform < 02-admin-tables.sql
mysql -u root -p esb_platform < 03-init-data.sql
# 可选
mysql -u root -p esb_platform < 04-demo-data.sql
```

或在 Navicat / DBeaver / DataGrip 中按顺序打开执行。

## 注意事项

1. `01` 中的表结构来自 JPA 实体反推，若你的生产库已存在这些表，**不要直接执行 ALTER**；先比对字段差异。
2. `02` 中所有表都使用 `CREATE TABLE IF NOT EXISTS`，重复执行不会报错，也不会覆盖数据。
3. `xrpc-admin` 启动时会由 `DataInitializer` 自动创建管理员账号 `admin / admin123`，因此 `03` 中没有写死密码哈希。
4. 如果确实要用 SQL 单独插入管理员，先用以下 Java 代码生成 BCrypt 哈希：

```java
System.out.println(new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode("admin123"));
```

然后执行：

```sql
INSERT IGNORE INTO `esb_admin_user`
  (`id`,`username`,`password_hash`,`salt`,`real_name`,`status`,`roles`,`create_time`)
VALUES
  (1,'admin','<上面生成的哈希>','','系统管理员','ENABLE','ADMIN',NOW());
```

5. 前端接真实后端时，把 `.env.development` 的 `VITE_USE_MOCK` 改为 `false`。
