package neatlogic.framework.auth.core;

import neatlogic.module.licensefixture.cs.portal.auth.CommercialAuthModuleFixture;
import org.junit.Test;

import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/** 验证模块归属仅由具体类的注解声明，不能由包路径、父类或方法覆盖提供。 */
public class AuthCSBaseTest {
    /** 商业子目录与权限组都不影响注解声明的模块。 */
    @Test
    public void readsExplicitModuleRegardlessOfPackageAndGroup() {
        CommercialAuthModuleFixture auth = new CommercialAuthModuleFixture();
        assertEquals("licensefixture", auth.getAuthModule());
        assertEquals("framework", auth.getAuthGroup());
        assertEquals("explicit-module", new FrameworkCommercialAuth().getAuthModule());
        assertEquals("explicit-module", AuthBase.getAuthModule(FrameworkCommercialAuth.class));
    }

    /** 子类和匿名类必须重新声明，不能继承父类或外部类的模块。 */
    @Test
    public void refusesMissingBlankAndInheritedDeclarations() {
        assertNull(new MissingModuleAuth().getAuthModule());
        assertNull(new BlankModuleAuth().getAuthModule());
        assertNull(new CommercialAuthModuleFixture() { }.getAuthModule());
        assertNull(AuthBase.getAuthModule(null));
        assertFalse(AuthFactory.isAuthLicenseValid(MissingModuleAuth.class));
        assertFalse(AuthFactory.isAuthLicenseValid(BlankModuleAuth.class));
    }

    /** 具体标签无法通过覆盖读取方法绕过声明规则。 */
    @Test
    public void moduleGetterCannotBeOverridden() throws Exception {
        assertTrue(Modifier.isFinal(AuthBase.class.getMethod("getAuthModule").getModifiers()));
        assertFalse(AuthModule.class.isAnnotationPresent(java.lang.annotation.Inherited.class));
    }

    /** 特殊位置的权限也使用显式模块，而不是包路径推导。 */
    @AuthModule("explicit-module")
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

    /** 故意不声明模块，用于验证具体子类不继承注解。 */
    public static class MissingModuleAuth extends FrameworkCommercialAuth { }

    /** 故意声明空值，用于验证空白不构成合法模块。 */
    @AuthModule("  ")
    public static class BlankModuleAuth extends FrameworkCommercialAuth { }
}
