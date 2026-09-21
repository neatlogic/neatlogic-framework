package neatlogic.framework.i18n;

import neatlogic.framework.asynchronization.threadlocal.RequestContext;
import org.springframework.lang.Nullable;

import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 国际化运行时唯一翻译入口，统一 Locale、查找、格式化和缺失降级语义。
 */
public class I18nTranslator {
    private final ModuleI18nCatalog catalog;
    private final MissingI18nReporter missingReporter;
    private final ConcurrentMap<TemplateKey, I18nMessageTemplate> templateMap = new ConcurrentHashMap<>();

    /** 使用指定目录和诊断器创建翻译服务。 */
    I18nTranslator(ModuleI18nCatalog catalog, MissingI18nReporter missingReporter) {
        this.catalog = catalog;
        this.missingReporter = missingReporter;
    }

    /** 使用当前请求语言翻译，缺失时将原始 key 作为兼容模板格式化。 */
    public String translate(String key, Object... args) {
        return translate(currentLocale(), key, args);
    }

    /** 使用指定语言翻译，缺失时将原始 key 作为兼容模板格式化。 */
    public String translate(Locale locale, String key, Object... args) {
        I18nMessageTemplate template = findTemplate(locale, key);
        if (template == null) {
            reportMissing(locale, key);
            return formatMissingKey(locale, key, args);
        }
        return template.format(args);
    }

    /** 翻译目录中已存在的 key；普通展示文案原样返回且不记录缺失诊断。 */
    public String translateIfPresent(String key, Object... args) {
        I18nMessageTemplate template = findTemplate(currentLocale(), key);
        return template == null ? key : template.format(args);
    }

    /** 查找并格式化已有消息，缺失时返回 null，供 Spring MessageSource 保持标准语义。 */
    @Nullable
    String translateExisting(Locale locale, String key, Object... args) {
        I18nMessageTemplate template = findTemplate(locale, key);
        return template == null ? null : template.format(args);
    }

    /** 在调用方确认所有回退路径均失败后记录一次缺失诊断。 */
    void reportMissing(Locale locale, String key) {
        missingReporter.report(I18nLocaleResolver.resolveLanguage(locale), key);
    }

    /** 返回当前请求语言；非请求线程和缺省请求统一使用中文。 */
    public Locale currentLocale() {
        RequestContext context = RequestContext.get();
        return I18nLocaleResolver.resolveLocale(context == null ? null : context.getLocale());
    }

    /** 返回目录，供诊断和兼容适配使用。 */
    public ModuleI18nCatalog getCatalog() {
        return catalog;
    }

    /** 缺失 key 不进入缓存，仅在存在参数时按消息模板格式化存量文案。 */
    private String formatMissingKey(Locale locale, String key, Object... args) {
        if (args == null || args.length == 0) {
            return key;
        }
        String language = I18nLocaleResolver.resolveLanguage(locale);
        return new I18nMessageTemplate(key, language).format(args);
    }

    /** 查找或创建不可变模板缓存。 */
    @Nullable
    private I18nMessageTemplate findTemplate(Locale locale, String key) {
        String language = I18nLocaleResolver.resolveLanguage(locale);
        TemplateKey templateKey = new TemplateKey(language, key);
        I18nMessageTemplate cached = templateMap.get(templateKey);
        if (cached != null) {
            return cached;
        }
        String message = catalog.findMessage(language, key);
        if (message == null) {
            return null;
        }
        I18nMessageTemplate template = new I18nMessageTemplate(message, language);
        I18nMessageTemplate existing = templateMap.putIfAbsent(templateKey, template);
        return existing == null ? template : existing;
    }

    /** 模板缓存键。 */
    private record TemplateKey(String language, String key) {
    }
}
