package neatlogic.framework.i18n;

import org.junit.Test;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.DefaultMessageSourceResolvable;

import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/** 验证缺失翻译诊断缓存的容量边界。 */
public class MissingI18nReporterTest {

    /** 不同动态 key 超过上限后不得继续占用去重缓存。 */
    @Test
    public void boundsMissingKeyCache() {
        MissingI18nReporter reporter = new MissingI18nReporter(true, 2);
        reporter.report("zh", "first\nkey");
        reporter.report("zh", "second");
        reporter.report("zh", "third");
        reporter.report("zh", "fourth");
        assertEquals(2, reporter.size());
        assertEquals(2L, reporter.getSuppressedCount());
    }

    /** 候选回退和默认文案不应误报，最终缺失才记录诊断。 */
    @Test
    public void reportsOnlyFinalMissingMessages() {
        MissingI18nReporter reporter = new MissingI18nReporter(true, 8);
        I18nTranslator translator = new I18nTranslator(ModuleI18nCatalog.getInstance(), reporter);
        ModuleJsonMessageSource source = new ModuleJsonMessageSource(translator);
        DefaultMessageSourceResolvable candidates = new DefaultMessageSourceResolvable(
                new String[]{"missing.candidate", "plain"});
        assertEquals("Can't find anything", source.getMessage(candidates, Locale.ENGLISH));
        assertEquals("fallback", source.getMessage(
                "missing.default", null, "fallback", Locale.ENGLISH));
        assertEquals(0, reporter.size());
        try {
            source.getMessage("missing.final", null, Locale.ENGLISH);
            fail("最终缺失应抛出 NoSuchMessageException");
        } catch (NoSuchMessageException ignored) {
            // 最终缺失由 Spring 消息源保持标准异常语义。
        }
        assertEquals(1, reporter.size());
        assertEquals("missing.static", translator.translate(Locale.ENGLISH, "missing.static"));
        assertEquals(2, reporter.size());
    }

    /** JVM 参数未配置、关闭或非法时均不得记录缺失诊断。 */
    @Test
    public void disablesReportingByDefaultAndForNonTrueValues() {
        String originalValue = System.getProperty(MissingI18nReporter.ENABLE_PROPERTY);
        try {
            for (String value : new String[]{null, "false", "invalid"}) {
                if (value == null) {
                    System.clearProperty(MissingI18nReporter.ENABLE_PROPERTY);
                } else {
                    System.setProperty(MissingI18nReporter.ENABLE_PROPERTY, value);
                }
                MissingI18nReporter reporter = new MissingI18nReporter();
                reporter.report("zh", "missing." + value);
                assertEquals(0, reporter.size());
                assertEquals(0L, reporter.getSuppressedCount());
            }
        } finally {
            restoreProperty(originalValue);
        }
    }

    /** JVM 参数为 true 时启用诊断，参数在实例创建后发生变化不影响当前实例。 */
    @Test
    public void enablesReportingOnlyAtInitialization() {
        String originalValue = System.getProperty(MissingI18nReporter.ENABLE_PROPERTY);
        try {
            System.setProperty(MissingI18nReporter.ENABLE_PROPERTY, "true");
            MissingI18nReporter reporter = new MissingI18nReporter();
            System.setProperty(MissingI18nReporter.ENABLE_PROPERTY, "false");
            reporter.report("zh", "missing.enabled");
            assertEquals(1, reporter.size());
        } finally {
            restoreProperty(originalValue);
        }
    }

    /** 关闭诊断不改变静态翻译和 Spring 消息源的缺失降级语义。 */
    @Test
    public void preservesMissingMessageSemanticsWhenDisabled() {
        MissingI18nReporter reporter = new MissingI18nReporter(false, 8);
        I18nTranslator translator = new I18nTranslator(ModuleI18nCatalog.getInstance(), reporter);
        ModuleJsonMessageSource source = new ModuleJsonMessageSource(translator);
        assertEquals("missing.static", translator.translate(Locale.ENGLISH, "missing.static"));
        assertEquals("fallback", source.getMessage("missing.default", null, "fallback", Locale.ENGLISH));
        try {
            source.getMessage("missing.final", null, Locale.ENGLISH);
            fail("关闭诊断后仍应保持 NoSuchMessageException 语义");
        } catch (NoSuchMessageException ignored) {
            // 日志开关只影响诊断输出，不改变 Spring 消息源契约。
        }
        assertEquals(0, reporter.size());
        assertEquals(0L, reporter.getSuppressedCount());
    }

    /** 恢复测试前的 JVM 参数，避免影响其他用例。 */
    private void restoreProperty(String originalValue) {
        if (originalValue == null) {
            System.clearProperty(MissingI18nReporter.ENABLE_PROPERTY);
        } else {
            System.setProperty(MissingI18nReporter.ENABLE_PROPERTY, originalValue);
        }
    }
}
