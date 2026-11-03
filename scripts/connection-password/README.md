# 基础连接密码升级与迁移

`neatlogic` 库的 `mongodb`、`elasticsearch`、`datasource` 三张表默认使用 `AES:` 保存新密码，读取兼容历史明文、`{RC4}`、`RC4:`、`{ENCRYPTED}`、`RSA:`、`RSA.AES:` 和 `{AES}`。AES 别名统一在处理器的 `handlers()` 列表维护，工厂识别及解密按该列表匹配；对外使用的 `RSA.AES:` 通过 `RSAAESCryptoHandler.ENCRYPTED_PREFIX` 公共常量引用，其余别名保留在列表中。已有受支持密文保存时保留原值。迁移工具只转换非空历史明文，不改变目标服务的实际账号密码。`AES:` 仅为存储标识，内部仍使用 RSA-OAEP 包装 AES 密钥、AES-GCM 加密密码；其他账号页面的传输协议仍可使用 `RSA.AES:`。

## 上线顺序

1. 备份 `neatlogic` 库，并验证备份可恢复。
2. 在主库执行 `src/main/resources/neatlogic/resources/framework/changelog/2026-11-03/neatlogic.sql` 中的三条字段扩容语句，将三个 `password` 字段改为可空 `TEXT`。也可由既有版本升级流程执行该脚本，但必须先于新代码写入密码完成。
3. 升级期间暂停连接配置修改，将所有实例升级到支持统一密码处理器的版本，再恢复保存新密码或执行迁移。保留当前版本所使用的 RSA 密钥配置，确保实例间能解密同一密文。
4. 执行预览，确认各表待转换数量；预览也要求字段扩容完成。
5. 明确授权后使用 `--apply` 执行迁移，再重复预览，确认待转换数量为零。
6. 验证租户 MySQL、MongoDB 和 Elasticsearch 连接。迁移不会主动重建应用已有连接池；应用重新加载配置时使用原始密码认证。

写入 AES 前缀密文后，应用回退必须使用仍支持该格式的版本。回退为不支持 AES 前缀或仅支持 RC4 的代码会导致认证失败。不要将密码字段缩回原长度。

## 手动运行

使用包含本次更新的应用运行时依赖。以下 `WEB-INF/lib` 路径必须替换成实际发布目录，并确认其中的 framework JAR 包含 `ConnectionPasswordMigrationTool`；不要加载旧版 framework JAR。

默认预览：

```sh
java -cp '/path/to/application/WEB-INF/lib/*' \
  neatlogic.framework.tools.ConnectionPasswordMigrationTool \
  --url 'jdbc:mysql://127.0.0.1:3306/neatlogic?characterEncoding=UTF-8' \
  --user migration_user
```

正式执行：

```sh
java -cp '/path/to/application/WEB-INF/lib/*' \
  neatlogic.framework.tools.ConnectionPasswordMigrationTool \
  --url 'jdbc:mysql://127.0.0.1:3306/neatlogic?characterEncoding=UTF-8' \
  --user migration_user \
  --apply
```

交互终端会提示输入数据库连接密码，不回显。非交互运行须通过部署环境注入 `NEATLOGIC_MIGRATION_PASSWORD`，不要把密码写入参数、JDBC URL 或仓库文件。连接账号需要查询这三张表及结构元数据的权限；正式执行还需要更新权限。工具仅接受 MySQL JDBC URL，连接时沿用环境所需的 TLS 参数。

输出仅包含每张表的待转换、已转换、跳过数量。空值、空白值以及已有受支持密文均跳过；跳过不表示已验证密文有效性。退出码为 `0` 表示本次操作成功，`1` 表示失败；错误输出不包含底层连接异常或密码内容。

## 事务与失败处理

工具要求三张表为 InnoDB 且密码字段至少为 TEXT 容量。预览不更新数据；正式执行在一个事务中更新全部候选记录。更新条件按租户标识与原密码字节精确匹配，并发修改或数据库错误会使整次迁移回滚。停止并发的配置修改有助于一次完成迁移。

字段未扩容时先完成扩容；发生并发冲突时重新预览后重试。遇到连接中断等提交结果不明确的情况，应重新预览确认结果，工具可重复运行。若备份恢复涉及这些配置，需同步核对应用版本与实际账号密码。

## 开发验证

从 `neatlogic-build-root` 使用 `develop` 配置构建 framework；master 属于 `commercial` reactor 配置，联编时同时启用：

```sh
mvn -Pdevelop,commercial -pl ../neatlogic-framework,../neatlogic-master -am -DskipTests compile
mvn -Pdevelop,commercial -pl ../neatlogic-framework,../neatlogic-master -am \
  -Dtest=ConnectionPasswordTest,ConnectionPasswordMigrationTest \
  -Dsurefire.failIfNoSpecifiedTests=false test
```

数据库集成测试仅在显式设置以下环境变量时运行，否则跳过：

- `NEATLOGIC_PASSWORD_TEST_URL`：连接本地测试 MySQL 的 JDBC URL。
- `NEATLOGIC_PASSWORD_TEST_USER`：可创建和删除测试库的账号。
- `NEATLOGIC_PASSWORD_TEST_PASSWORD`：该账号密码。

集成测试创建随机命名的 `nl_pwd_test_` 临时库，验证完成后删除该测试库，不更新业务库。测试覆盖真实扩容 SQL、默认预览、明文转换、已有格式保留、重复执行、写入失败回滚及并发修改保护。

## 修改文件清单

以下路径以对应仓库为根目录。

| 仓库 | 文件 | 用途 |
| --- | --- | --- |
| neatlogic-framework | `src/main/java/neatlogic/framework/util/ConnectionPasswordUtil.java` | 共用密码转换 |
| neatlogic-framework | `src/main/java/neatlogic/framework/crypto/handler/RC4CryptoHandler.java` | 历史前缀兼容 |
| neatlogic-framework | `src/main/java/neatlogic/framework/crypto/handler/RSAAESCryptoHandler.java` | AES别名列表及统一解密 |
| neatlogic-framework | `src/main/java/neatlogic/framework/dto/DatasourceVo.java`、`MongoDbVo.java`、`ElasticsearchVo.java` | 读取、保存与缓存更新 |
| neatlogic-framework | `src/main/java/neatlogic/framework/dao/mapper/MongoDbMapper.xml`、`ElasticsearchMapper.xml` | 密文落库，ES配置字段对齐 |
| neatlogic-framework | `src/main/java/neatlogic/framework/tools/ConnectionPasswordMigrationTool.java` | 手动迁移入口 |
| neatlogic-framework | `src/main/resources/neatlogic/resources/framework/changelog/2026-11-03/neatlogic.sql` | 现有库字段扩容 |
| neatlogic-framework | `src/test/java/neatlogic/framework/util/ConnectionPasswordTest.java` | 密码与映射契约测试 |
| neatlogic-framework | `src/test/java/neatlogic/framework/tools/ConnectionPasswordMigrationTest.java` | 独立数据库迁移及真实Mapper测试 |
| neatlogic-framework | `scripts/connection-password/README.md` | 运行与上线说明 |
| neatlogic-database | `mysql/neatlogic.sql` | 初始化库结构同步 |
| neatlogic-cmdb | `src/main/java/neatlogic/module/cmdb/api/resourcecenter/account/AccountGetApi.java` | 引用默认传输前缀公共常量选择处理器 |
