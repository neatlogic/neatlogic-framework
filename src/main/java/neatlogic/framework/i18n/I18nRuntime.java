package neatlogic.framework.i18n;

import neatlogic.framework.exception.module.ModuleInitRuntimeException;

/**
 * 为 Spring 启动前调用和兼容静态入口提供唯一运行时实例。
 */
public final class I18nRuntime {
    private static final I18nTranslator TRANSLATOR = initializeTranslator();

    /** 工具类不允许实例化。 */
    private I18nRuntime() {
    }

    /** 返回全局共享的翻译服务。 */
    public static I18nTranslator getTranslator() {
        return TRANSLATOR;
    }

    /** 初始化全局翻译服务，非法语言资源必须在被静态初始化包装前输出原始异常并终止 JVM。 */
    private static I18nTranslator initializeTranslator() {
        try {
            return new I18nTranslator(ModuleI18nCatalog.getInstance(), new MissingI18nReporter());
        } catch (ModuleInitRuntimeException ex) {
            ex.printStackTrace(System.out);
            System.out.flush();
            System.exit(1);
            throw ex;
        }
    }
}
