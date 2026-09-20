package neatlogic.framework.i18n;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 保存按模块和语言组织的不可变翻译目录，不承担加载、格式化和诊断职责。
 */
public final class ModuleI18nCatalog {
    private static volatile ModuleI18nCatalog instance;

    private final Map<String, Map<String, Map<String, String>>> ownerLanguageMessageMap;
    private final Map<String, Map<String, String>> messageMap;
    private final Map<String, Map<String, String>> originMap;

    /** 获取当前 ClassLoader 对应的语言目录单例。 */
    public static ModuleI18nCatalog getInstance() {
        ModuleI18nCatalog result = instance;
        if (result == null) {
            synchronized (ModuleI18nCatalog.class) {
                result = instance;
                if (result == null) {
                    result = new ModuleI18nCatalog(Thread.currentThread().getContextClassLoader());
                    instance = result;
                }
            }
        }
        return result;
    }

    /** 扫描指定 ClassLoader 中的模块语言资源。 */
    ModuleI18nCatalog(ClassLoader classLoader) {
        ModuleI18nResourceLoader.LoadedResources resources = new ModuleI18nResourceLoader(classLoader).load();
        this.ownerLanguageMessageMap = immutableNestedMap(resources.ownerLanguageMessages());
        this.messageMap = immutableMap(resources.globalMessages());
        this.originMap = immutableMap(resources.globalOrigins());
    }

    /** 获取原始翻译，缺失时返回 null。 */
    public String findMessage(String language, String key) {
        Map<String, String> languageMessages = messageMap.get(language);
        return languageMessages == null ? null : languageMessages.get(key);
    }

    /** 返回翻译来源，主要用于诊断冲突与缺失问题。 */
    public String getOrigin(String language, String key) {
        Map<String, String> languageOrigins = originMap.get(language);
        return languageOrigins == null ? null : languageOrigins.get(key);
    }

    /** 返回按 owner 和语言隔离的不可变资源视图。 */
    public Map<String, Map<String, Map<String, String>>> getOwnerLanguageMessageMap() {
        return ownerLanguageMessageMap;
    }

    /** 创建两层不可变映射。 */
    private static Map<String, Map<String, String>> immutableMap(Map<String, Map<String, String>> source) {
        Map<String, Map<String, String>> result = new LinkedHashMap<>();
        source.forEach((key, value) -> result.put(key,
                Collections.unmodifiableMap(new LinkedHashMap<>(value))));
        return Collections.unmodifiableMap(result);
    }

    /** 创建三层不可变映射。 */
    private static Map<String, Map<String, Map<String, String>>> immutableNestedMap(
            Map<String, Map<String, Map<String, String>>> source) {
        Map<String, Map<String, Map<String, String>>> result = new LinkedHashMap<>();
        source.forEach((owner, languages) -> result.put(owner, immutableMap(languages)));
        return Collections.unmodifiableMap(result);
    }
}
