# 权限模块声明与商业授权规则

## 必须显式声明所属模块

每个具体权限标签必须在自身类上声明 `neatlogic.framework.auth.core.AuthModule`，包括普通权限、商业权限和 `NoAuth`：

```java
@AuthModule("informant")
public class INFORMANT_BASE extends AuthCSBase {
}
```

注解只用于类、保留到运行期且不继承。子类即使继承已声明的权限，也必须自己声明。注解缺失、值为空或仅含空白时，标签不注册、不参与鉴权或权限展开；注册阶段记录包含类名的中文错误日志，其他合法标签继续加载。

`AuthBase#getAuthModule()` 已改为 `final`，与静态重载 `AuthBase.getAuthModule(Class<? extends AuthBase>)` 共用具体类的注解读取逻辑。类级读取不实例化标签、不触发权限工厂初始化。不得再覆盖该方法，也不根据包名、目录、权限组或父类推断所属模块。

模块与权限组可以不同。例如 Portal 声明 `tenant`、权限组为 `framework`；Diagram 声明 `diagram`，不受 `cmdb.cs.diagram` 包路径影响。注解值采用真实模块边界，迁移清单见 [auth-module-migration.json](auth-module-migration.json)。

## 鉴权规则

`AuthFactory.isAuthLicenseValid(Class<? extends AuthBase>)` 先检查所有权限标签的模块声明；商业权限还必须已正确注册，且所属模块没有被 `ModuleUtil` 标记为授权无效。商业模块继续负责启动授权登记、有效期及续期生效规则，框架不在每次鉴权时重新解析 License。

统一入口先过滤无效目标，再处理身份豁免、系统默认权限和数据库授权。历史用户、角色及用户组数据不能使未声明或失效的商业标签重新生效；父子权限递归与展示展开也不能穿过无效标签继续授予权限。

- 多权限保持“任选其一”：仍有合法且符合授权条件的标签即可通过；只有无效标签时拒绝。
- 声明合法的 `NoAuth` 保持原有放行语义。
- `master` API 仍豁免原有用户权限和商业 License 判断，但不豁免模块声明；未声明标签不能在 master 生效。
- 没有声明 `@AuthAction` 的 API 保持原有行为。
- API 权限拒绝沿用既有响应结构。

这些规则覆盖框架 API 执行链及直接 `AuthActionChecker` 调用，与 API Bean 是否位于 `cs` 包无关。后台任务、异步内部调用及直接 Service 调用继续保留所属模块的商业切面或显式 License 检查。

## 迁移与发布

此次为 41 个仓库的 257 个生产标签补充注解。按用户要求排除废弃的 Diagnosis 模块，不改其配置或标签。保留所有权限名称、类路径、权限组、包含关系及系统用户默认授权，不删除数据库授权记录，也不新增模块安装状态校验。

框架与受影响模块应在同一版本中重新构建、同步发布。旧模块没有注解的权限将按规则失效；仍覆盖旧 `getAuthModule()` 方法的二进制与新框架不兼容，不能混用。部署重启后刷新浏览器，以重新取得权限和菜单。

## 审计与验证

只读扫描生产标签并输出逐标签清单：

```sh
python3 neatlogic-framework/scripts/audit_auth_modules.py --workspace /path/to/codedriver --json /tmp/auth-modules.json
```

审计不扫描测试、聚合副本、符号链接重复及已排除的 Diagnosis。新模块应人工确认真实归属并更新审计映射，不能用包名推导代替声明。

本次构建、测试、迁移范围与既有构建阻碍见 [auth-module-migration.md](auth-module-migration.md)。无数据库迁移，未部署、未修改客户数据。
