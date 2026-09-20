package neatlogic.framework.i18n;

import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.assertEquals;

/** 验证语言标签解析和默认回退策略。 */
public class I18nLocaleResolverTest {

    /** 英文国家标签和下划线写法均应归一化为英文。 */
    @Test
    public void normalizesSupportedLanguageTags() {
        assertEquals(Locale.ENGLISH, I18nLocaleResolver.parse("en-US"));
        assertEquals(Locale.ENGLISH, I18nLocaleResolver.parse("en_US"));
        assertEquals(Locale.CHINESE, I18nLocaleResolver.parse("zh-CN"));
    }

    /** 空值和不支持的语言必须稳定回退中文，不依赖 JVM 默认值。 */
    @Test
    public void fallsBackToChinese() {
        assertEquals(Locale.CHINESE, I18nLocaleResolver.parse(null));
        assertEquals(Locale.CHINESE, I18nLocaleResolver.parse("fr-FR"));
        assertEquals("zh", I18nLocaleResolver.resolveLanguage(null));
    }
}
