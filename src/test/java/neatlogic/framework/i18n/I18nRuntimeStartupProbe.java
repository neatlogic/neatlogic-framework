package neatlogic.framework.i18n;

/** 在独立 JVM 中触发翻译运行时初始化。 */
public final class I18nRuntimeStartupProbe {
    /** 工具类不允许实例化。 */
    private I18nRuntimeStartupProbe() {
    }

    /** 启动翻译运行时，初始化成功时输出完成标记。 */
    public static void main(String[] args) {
        I18nRuntime.getTranslator();
        System.out.println("I18N_RUNTIME_INITIALIZED");
    }
}
