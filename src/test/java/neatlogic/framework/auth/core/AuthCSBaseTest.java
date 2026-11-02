package neatlogic.framework.auth.core;

import neatlogic.module.licensefixture.cs.portal.auth.CommercialAuthModuleFixture;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/** 验证商业包移动和子目录不会改变权限对应的 License 模块。 */
public class AuthCSBaseTest {
    /** 商业子目录的标签属于包根模块，不能被识别为 cs、portal 或权限组。 */
    @Test
    public void resolvesModuleBeforeCommercialSubdirectories() {
        CommercialAuthModuleFixture auth = new CommercialAuthModuleFixture();
        assertEquals("licensefixture", auth.getAuthModule());
        assertEquals("framework", auth.getAuthGroup());
    }

    /** base 模块中框架命名空间的商业标签仍解析所属模块。 */
    @Test
    public void resolvesFrameworkPackageModule() {
        assertEquals("auth", new FrameworkCommercialAuth().getAuthModule());
    }

    /** 标签显式覆盖模块时，保持原有覆盖优先级。 */
    @Test
    public void preservesExplicitModuleOverride() {
        CommercialAuthModuleFixture auth = new CommercialAuthModuleFixture() {
            /** 显式模块约定优先于默认包路径识别。 */
            @Override public String getAuthModule() { return "explicit-module"; }
        };
        assertEquals("explicit-module", auth.getAuthModule());
    }

    /** 此标签位于 neatlogic.framework.auth 包下，模拟 base 模块标签。 */
    public static class FrameworkCommercialAuth extends AuthCSBase {
        /** 返回测试显示名。 */
        @Override public String getAuthDisplayName() { return "fixture"; }
        /** 返回测试说明。 */
        @Override public String getAuthIntroduction() { return "fixture"; }
        /** 返回测试权限组。 */
        @Override public String getAuthGroup() { return "framework"; }
        /** 返回测试排序。 */
        @Override public Integer getSort() { return 0; }
    }
}
