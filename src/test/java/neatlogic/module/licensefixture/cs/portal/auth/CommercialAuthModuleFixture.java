package neatlogic.module.licensefixture.cs.portal.auth;

import neatlogic.framework.auth.core.AuthCSBase;
import neatlogic.framework.auth.core.AuthModule;

/** 使用真实商业包层次验证归属，不依赖商业模块的实现依赖。 */
@AuthModule("licensefixture")
public class CommercialAuthModuleFixture extends AuthCSBase {
    /** 测试显示名不参与外部语言资源加载。 */
    @Override public String getAuthDisplayName() { return "fixture"; }
    /** 返回测试说明。 */
    @Override public String getAuthIntroduction() { return "fixture"; }
    /** 权限组可以不同于所属模块，不能用权限组替代 License 模块标识。 */
    @Override public String getAuthGroup() { return "framework"; }
    /** 返回测试排序。 */
    @Override public Integer getSort() { return 0; }
}
