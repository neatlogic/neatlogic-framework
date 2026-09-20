package neatlogic.framework.i18n;

import java.util.Locale;
import java.util.Set;

/**
 * 统一解析国际化语言，避免请求、静态入口和消息源各自维护回退规则。
 */
public final class I18nLocaleResolver {
    public static final String DEFAULT_LANGUAGE = "zh";
    public static final Locale DEFAULT_LOCALE = Locale.forLanguageTag(DEFAULT_LANGUAGE);
    private static final Set<String> SUPPORTED_LANGUAGES = Set.of(DEFAULT_LANGUAGE, "en");

    /** 工具类不允许实例化。 */
    private I18nLocaleResolver() {
    }

    /** 将 Locale 归一化为系统支持的语言。 */
    public static String resolveLanguage(Locale locale) {
        if (locale != null) {
            String language = locale.getLanguage().toLowerCase(Locale.ROOT);
            if (SUPPORTED_LANGUAGES.contains(language)) {
                return language;
            }
        }
        return DEFAULT_LANGUAGE;
    }

    /** 返回只包含受支持语言的 Locale，国家和变体不参与资源选择。 */
    public static Locale resolveLocale(Locale locale) {
        return Locale.forLanguageTag(resolveLanguage(locale));
    }

    /** 解析 Cookie 等外部来源的语言标签，兼容连字符和下划线写法。 */
    public static Locale parse(String languageTag) {
        if (languageTag == null || languageTag.isBlank()) {
            return DEFAULT_LOCALE;
        }
        Locale locale = Locale.forLanguageTag(languageTag.trim().replace('_', '-'));
        return resolveLocale(locale);
    }
}
