# 显式权限模块注解迁移记录

日期：2026-11-02。范围为 257 个生产权限标签、41 个真实模块仓库；废弃 Diagnosis 按用户要求排除。

## 迁移内容

所有标签均直接声明 `@AuthModule`，模块归属仅从具体类注解读取。删除包名推导及 3 个生产标签的旧方法覆盖，框架读取方法改为 final。框架测试夹具和 Tenant 的权限继承夹具同步迁移，并保留缺失、空白、子类未声明的刻意负例。

逐标签路径、完整类名、模块声明和所属仓库见 [机器清单](auth-module-migration.json)。独立审计脚本与清单集合逐项比对一致，257 项、41 仓库、零错误。

## 仓库与声明

| 仓库 | 模块声明 | 标签数 |
| --- | --- | ---: |
| `neatlogic-ai-base` | `ai` | 9 |
| `neatlogic-alert-base` | `alert` | 19 |
| `neatlogic-alert-commercial` | `alert` | 4 |
| `neatlogic-approval-base` | `approval` | 5 |
| `neatlogic-autoexec-base` | `autoexec` | 17 |
| `neatlogic-autoexec-commercial` | `autoexec` | 1 |
| `neatlogic-change` | `change` | 1 |
| `neatlogic-cmdb-base` | `cmdb` | 21 |
| `neatlogic-cmdb-commercial` | `cmdb` | 2 |
| `neatlogic-cmdb-transfer` | `cmdb` | 1 |
| `neatlogic-codehub-base` | `codehub` | 3 |
| `neatlogic-credit` | `credit` | 4 |
| `neatlogic-dashboard-base` | `dashboard` | 2 |
| `neatlogic-deploy-base` | `deploy` | 8 |
| `neatlogic-diagram` | `diagram` | 6 |
| `neatlogic-dr-base` | `dr` | 5 |
| `neatlogic-edu` | `edu` | 6 |
| `neatlogic-eoa-base` | `eoa` | 1 |
| `neatlogic-event` | `event` | 2 |
| `neatlogic-fdbank` | `fdbank` | 2 |
| `neatlogic-fileservice-base` | `fileservice` | 3 |
| `neatlogic-framework` | `framework` | 37 |
| `neatlogic-haday` | `haday` | 11 |
| `neatlogic-informant` | `informant` | 1 |
| `neatlogic-informant-base` | `informant` | 4 |
| `neatlogic-inspect-base` | `inspect` | 12 |
| `neatlogic-itsm-base` | `process` | 14 |
| `neatlogic-itsm-commercial` | `process` | 4 |
| `neatlogic-knowledge` | `knowledge` | 4 |
| `neatlogic-knowledge-base` | `knowledge` | 1 |
| `neatlogic-master` | `master` | 3 |
| `neatlogic-monitor-base` | `monitor` | 19 |
| `neatlogic-oncall` | `oncall` | 3 |
| `neatlogic-pbc` | `pbc` | 2 |
| `neatlogic-rdm-base` | `rdm` | 4 |
| `neatlogic-report` | `report` | 5 |
| `neatlogic-resourcepool` | `resourcepool` | 4 |
| `neatlogic-suzhoubank` | `suzhoubank` | 2 |
| `neatlogic-tagent-base` | `tagent` | 2 |
| `neatlogic-tenant-commercial` | `tenant` | 2 |
| `neatlogic-tlcbbank` | `tlcbbank` | 1 |

## 验证结果

- 既有 reactor 覆盖的 34 个受影响仓库编译通过（同时构建所需依赖模块）；临时聚合 reactor 另验证 approval-base、credit、eoa-base 编译通过，合计 37 个受影响仓库整模块编译通过。
- 全部 257 个生产标签使用当前框架执行独立 Java 17 编译通过，并从所得类文件逐项验证直接运行期注解及 `AuthBase.getAuthModule(Class)` 读取值均与清单一致。
- 关键回归共 96 个测试通过：框架 41、Informant 35、Tenant 权限 API 15、Autoexec 商业授权 4、Haday API 契约 1，零失败。
- 回归覆盖缺失/空声明、父类注解不继承、final 约束、历史授权、特殊身份、递归、多权限和 NoAuth；对象、Raw、JSON 流、二进制和 SSE 五类 API 在普通租户及 master 下均拒绝未声明标签，master 的其他既有豁免保持。
- 本次迁移源码、框架及 Tenant 测试的已暂存/未暂存 diff 检查通过；Haday 其他字体文件的工作区空行告警属于范围外改动，未处理。
- 工作区级 i18n 扫描已有独立模块与聚合目录重复收集的问题，测试使用 `-Dexec.skip=true` 隔离 exec 校验，JUnit 照常执行。

额外旧模块的完整构建结果（未改动真实 POM 或无关业务）：

| 模块 | 结果与限制 |
| --- | --- |
| `neatlogic-edu` | 旧父 POM 范围 `[3.0.0,4.0.0)` 仍选择旧框架，无法解析新增注解。需升级其框架依赖后才能完整构建；此次标签对当前框架独立编译通过。 |
| `neatlogic-fdbank` | 既有接口缺少 `@AuthAction`，并引用缺失的 validation/cache 等旧类型，整模块编译失败；未修改这些业务代码。 |
| `neatlogic-suzhoubank` | 既有接口与当前框架的 API/方法签名不兼容，整模块编译失败；未修改这些业务代码。 |
| `neatlogic-tlcbbank` | 既有接口使用已不存在的 `AuthUser` 等类型，整模块编译失败；未修改这些业务代码。 |

临时聚合使用 `/tmp` 构建文件，不修改永久构建配置。上述四个模块仍不能声称已通过整模块构建，不应直接混入发布包。

尚未执行部署、数据库变更或已部署环境浏览器验收。

## 构建与上线注意

需同步重新构建并部署框架与受影响模块，禁止混用覆盖旧 getter 的二进制。历史权限数据不变；未迁移的外部标签按要求不生效。

41 个仓库中部分原有工作区已有改动，本次没有重置、清理或提交这些改动。根 `AGENTS.md` 已同步明确注解开发规范。
