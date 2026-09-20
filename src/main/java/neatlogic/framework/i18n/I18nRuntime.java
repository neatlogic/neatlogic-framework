package neatlogic.framework.i18n;

/**
 * 为 Spring 启动前调用和兼容静态入口提供唯一运行时实例。
 */
public final class I18nRuntime {
    private static final I18nTranslator TRANSLATOR = new I18nTranslator(
            ModuleI18nCatalog.getInstance(), new MissingI18nReporter());

    /** 工具类不允许实例化。 */
    private I18nRuntime() {
    }

    /** 返回全局共享的翻译服务。 */
    public static I18nTranslator getTranslator() {
        return TRANSLATOR;
    }
}
