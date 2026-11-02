# 商业权限鉴权规则

## 授权边界

商业权限标签继承 `AuthCSBase`。标准 `neatlogic.module.<模块>` 和 `neatlogic.framework.<模块>` 包中的标签按包根识别所属模块，不把 `cs`、`portal` 等子目录或权限组误认为 License 模块；标签显式覆盖 `getAuthModule()` 时仍以覆盖值为准。所属商业模块负责在权限工厂初始化前，根据有效 License 登记模块授权状态；框架复用该状态，不在每次鉴权时重新读取密钥或解析 License。

`AuthFactory.isAuthLicenseValid(Class<? extends AuthBase>)` 用于判断权限的商业授权有效性。非商业权限保持原有行为；商业权限必须已正确注册，且所属模块没有被 `ModuleUtil` 标记为授权无效。无效或未注册的商业权限不能作为鉴权目标，也不能通过父子权限关系授予其他权限。

`AuthActionChecker` 先过滤不可用商业目标，再处理用户身份、出厂默认权限和数据库授权。因此，普通租户中的超级管理员、系统用户及维护账号也不能使用失效的商业权限。

## 历史行为与兼容

历史实现中，超级管理员可以在未核对商业目标时直接放行，数据库中的权限名称直接匹配也可能绕过商业权限注册表。当前规则在这些放行分支之前校验商业权限。

数据库中的用户、角色和用户组授权记录不删除、不更新。模块授权恢复并按模块原有机制重启后，仍按原授权记录判断访问能力。

- 多权限保持“任选其一”：失效商业权限不参与匹配；其他有效且已授予的权限仍可放行。
- 明确声明的 `NoAuth` 保持原有语义。
- API 层已有的 `master` 租户鉴权豁免保留，不向其他租户或直接 `AuthActionChecker` 调用新增豁免。
- 非商业权限原有的超级管理员、维护账号、默认授权及递归包含规则保持兼容。
- API 权限拒绝沿用现有响应，不新增参数或修改响应格式。

## 模块接入要求

继承 `AuthCSBase` 和启动登记商业授权状态是配套要求。该限制覆盖经过 `AuthActionChecker` 的调用及框架 API 执行链，与 API Bean 是否位于 `cs` 包无关。

后台任务和直接 Service 调用仍需所属模块的商业切面或显式 License 检查。模块有效期、宽限期、额度及续期刷新继续由所属商业模块负责，框架不新增 License 热更新或到期轮询机制。

## 验证

从 `neatlogic-build-root` 执行框架权限与实际 API 执行链测试，再回归 Informant 权限注册和节点数量控制。Informant 位于 `commercial` profile，需与 `develop` 同时启用。

工作区级 i18n 校验若因独立模块和聚合目录重复收集同一资源而失败，应单独记录；可使用 `-Dexec.skip=true` 隔离该既有构建问题，不能用跳过测试代替鉴权验证。

无需数据库迁移。上线更新框架并重启服务，配合各商业模块原有的 License 生效流程；前端已打开页面需刷新以重新取得权限和菜单。

### 2026-11-02 验证记录

- 框架 reactor 编译通过。
- 框架相关 37 个测试通过：权限检查器 22、原 API 公共执行链 4、商业 API 执行链 6、商业包归属 3、系统用户授权 Mapper 2。
- Informant 相关 35 个测试通过：权限注册 4、License 解析 2、额度服务 6、商业切面 7、注册集成 8、事件主题 8。
- 商业 API 执行链覆盖对象、Raw、JSON 流、二进制下载及 SSE，拒绝时验证业务方法没有执行；同时验证有效授权、用户无权限、master 豁免、其他商业/普通权限及 NoAuth 的替代分支。
- 标准测试构建仍在工作区 i18n 校验阶段因 `alert.alert.teammemberaccount` 重复收集而失败，来源为独立模块与 `neatlogic-itom-all` 聚合目录。联合回归使用 `-Dexec.skip=true`，JUnit 未跳过。
- `git diff --check` 和 `git diff --cached --check` 通过。未执行部署、数据库变更或已部署环境浏览器验收。

联合回归命令：

```sh
mvn -Pdevelop,commercial -pl ../neatlogic-informant -am -Dexec.skip=true -Dsurefire.failIfNoSpecifiedTests=false '-Dtest=AuthActionCheckerTest,ApiAuthExecutionChainTest,CommercialApiAuthExecutionChainTest,AuthCSBaseTest,UserMapperSystemUserAuthTest,InformantLicensePermissionRegistrarTest,InformantLicense*Test,RegisterInformantAgentApiTest,InformantCommercialAspectTest,InformantIncidentTopicTest' test
```

本次变更文件：

- `src/main/java/neatlogic/framework/auth/core/AuthFactory.java`
- `src/main/java/neatlogic/framework/auth/core/AuthActionChecker.java`
- `src/main/java/neatlogic/framework/auth/core/AuthCSBase.java`
- `src/test/java/neatlogic/framework/auth/core/AuthActionCheckerTest.java`
- `src/test/java/neatlogic/framework/auth/core/CommercialApiAuthExecutionChainTest.java`
- `src/test/java/neatlogic/framework/auth/core/AuthCSBaseTest.java`
- `src/test/java/neatlogic/module/licensefixture/cs/portal/auth/CommercialAuthModuleFixture.java`
- `docs/commercial-auth.md`
