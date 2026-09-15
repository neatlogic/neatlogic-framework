package neatlogic.framework.i18n;

import neatlogic.framework.util.I18nUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.text.MessageFormat;
import java.util.Locale;

import static org.junit.Assert.*;

/** 使用隔离语言资源验证 Spring 缓存和静态翻译入口的实际行为。 */
public class I18nQuoteIntegrationTest {
    private static final Locale TEST_LOCALE = new Locale("en", "XQ");
    private Locale previousLocale;
    private InspectableMessageSource source;

    /** 使用专用地区资源，避免覆盖运行环境中的中英文语言包。 */
    @Before
    public void setUp() {
        previousLocale = Locale.getDefault();
        source = new InspectableMessageSource();
        source.setBasename("classpath:i18n/language");
        source.setDefaultEncoding("UTF-8");
        source.setFallbackToSystemLocale(false);
    }

    /** 静态入口会设置默认语言，测试结束后必须恢复。 */
    @After
    public void tearDown() {
        Locale.setDefault(previousLocale);
    }

    /** 两个入口应输出相同文案，缓存对象可复用且参数不残留。 */
    @Test
    public void formatsBothEntrypointsAndReusesCache() {
        String[] keys = {"quoted", "escaped", "apostrophe"};
        String[] expected = {"Model 'Server' does not exist", "Model 'Server' does not exist", "Can't find Server"};
        for (int i = 0; i < keys.length; i++) {
            assertEquals(expected[i], source.getMessage(keys[i], new Object[]{"Server"}, TEST_LOCALE));
            assertEquals(expected[i], I18nUtils.getStaticMessage(TEST_LOCALE, keys[i], "Server"));
        }
        MessageFormat cached = source.formatFor("quoted");
        assertEquals("Model 'Switch' does not exist", source.getMessage("quoted", new Object[]{"Switch"}, TEST_LOCALE));
        assertSame(cached, source.formatFor("quoted"));
        String argument = "O'Brien {0} $1 \\path";
        String output = "Model '" + argument + "' does not exist";
        assertEquals(output, source.getMessage("quoted", new Object[]{argument}, TEST_LOCALE));
        assertEquals(output, I18nUtils.getStaticMessage(TEST_LOCALE, "quoted", argument));
    }

    /** 截图中的四个位置必须分别使用对应参数，不能残留占位符。 */
    @Test
    public void formatsScreenshotMessage() {
        Object[] args = {"Server", "server", "IP Address", "ip"};
        String expected = "The value of the unique rule attribute 'IP Address' (ip) of model 'Server (server)' is empty";
        assertEquals(expected, source.getMessage("screenshot", args, TEST_LOCALE));
        assertEquals(expected, I18nUtils.getStaticMessage(TEST_LOCALE, "screenshot", args));
    }

    /** Spring 无参数路径仍返回原文，缓存预处理不能污染资源内容。 */
    @Test
    public void preservesRawMessagesAndMissingKeys() {
        source.getMessage("quoted", new Object[]{"Server"}, TEST_LOCALE);
        assertEquals("Model '{0}' does not exist", source.getMessage("quoted", null, TEST_LOCALE));
        assertEquals("Can't find anything", source.getMessage("plain", new Object[0], TEST_LOCALE));
        // 静态入口原本始终使用 MessageFormat，此处确认无占位符文案仍沿用原生结果。
        assertEquals(MessageFormat.format("Can't find anything", new Object[0]),
                I18nUtils.getStaticMessage(TEST_LOCALE, "plain"));
        assertEquals("missing", I18nUtils.getStaticMessage(TEST_LOCALE, "missing", "Server"));
        // 当前消息源会把缺失 key 编译成模板，而不是采用默认文案，保持该行为。
        assertEquals("missing", source.getMessage("missing", new Object[]{"Server"}, "fallback", TEST_LOCALE));
        assertEquals("missing {0}", source.getMessage("missing '{0}'", new Object[]{"Server"}, TEST_LOCALE));
        assertEquals("missing {0}", I18nUtils.getStaticMessage(TEST_LOCALE, "missing '{0}'", "Server"));
    }

    /** 高级格式继续采用原生语义，不将被引用的占位符转换为参数。 */
    @Test
    public void preservesAdvancedFormat() {
        Object[] args = {"Server", 1234};
        String expected = new MessageFormat("'{0}' {1,number,integer}", TEST_LOCALE).format(args);
        assertEquals(expected, source.getMessage("complex", args, TEST_LOCALE));
    }

    /** 仅为断言缓存身份暴露受保护入口，不改变生产解析行为。 */
    private static class InspectableMessageSource extends ReloadableJsonBundleMessageSource {
        /** 返回指定翻译的实际缓存格式对象。 */
        MessageFormat formatFor(String key) {
            return resolveCode(key, TEST_LOCALE);
        }
    }
}
