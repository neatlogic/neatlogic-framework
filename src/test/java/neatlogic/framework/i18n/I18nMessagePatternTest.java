package neatlogic.framework.i18n;

import org.junit.Test;

import java.text.MessageFormat;
import java.util.Locale;

import static org.junit.Assert.*;

/** 验证普通模板的单引号兼容规则及复杂模板的原生语义边界。 */
public class I18nMessagePatternTest {
    /** 单引号、已有转义和撇号均应按显示字符处理。 */
    @Test
    public void formatsOrdinaryQuotes() {
        assertEquals("Model 'Server' does not exist", format("Model '{0}' does not exist", "Server"));
        assertEquals("Model 'Server' does not exist", format("Model ''{0}'' does not exist", "Server"));
        assertEquals("Can't find Server", format("Can't find {0}", "Server"));
        assertEquals("'Server", format("'{0}", "Server"));
        assertEquals("Server'", format("{0}'", "Server"));
        assertEquals("Server", format("{0}", "Server"));
    }

    /** 连续单引号保留已有转义对，并且重复预处理不得改变结果。 */
    @Test
    public void preservesPairsAndIsIdempotent() {
        for (int count = 1; count <= 8; count++) {
            String quotes = new String(new char[count]).replace('\0', '\'');
            String pattern = quotes + "{0}" + quotes;
            String normalized = I18nMessagePattern.normalize(pattern);
            String visible = new String(new char[(count + 1) / 2]).replace('\0', '\'');
            assertEquals(visible + "IP" + visible, format(pattern, "IP"));
            assertEquals(normalized, I18nMessagePattern.normalize(normalized));
        }
    }

    /** 参数中的引号、大括号和替换特殊字符不能被再次解析。 */
    @Test
    public void preservesArgumentText() {
        String argument = "O'Brien {0} $1 \\path";
        assertEquals("Value '" + argument + "'", format("Value '{0}'", argument));
    }

    /** 复杂格式、非编号大括号及无占位符文案必须原样交还调用方。 */
    @Test
    public void leavesOtherPatternsUnchanged() {
        String[] patterns = {
                "Can't find anything", "{0}", "'{name}' {0}", "'${...}' {0}",
                "{'key':'value'} {0}", "'{' {0} '}'", "'{{0}}'", "'{0}' {1,number}",
                "'{0}' {1,date,yyyy-MM-dd}", "'{0}' {1,time,HH:mm}",
                "'{0}' {1,choice,0#none|1#one}", "'{-1}'", "'{0'"
        };
        for (String pattern : patterns) {
            assertSame(pattern, I18nMessagePattern.normalize(pattern));
        }
        assertNull(I18nMessagePattern.normalize(null));
        String advanced = "'{0}' {1,number,integer}";
        assertEquals(new MessageFormat(advanced, Locale.ENGLISH).format(new Object[]{"IP", 1234}),
                new MessageFormat(I18nMessagePattern.normalize(advanced), Locale.ENGLISH)
                        .format(new Object[]{"IP", 1234}));
    }

    /** 同一参数混用显式格式和普通占位符时必须保留原始类型。 */
    @Test
    public void preservesArgumentTypeWhenAnyExplicitFormatExists() {
        I18nMessageTemplate explicitFirst = new I18nMessageTemplate("{0,number,integer} / {0}", "en");
        I18nMessageTemplate explicitLast = new I18nMessageTemplate("{0} / {0,number,integer}", "en");
        assertEquals("1,234 / 1,234", explicitFirst.format(1234));
        assertEquals("1,234 / 1,234", explicitLast.format(1234));
        assertEquals("1234 / 1234", new I18nMessageTemplate("{0} / {0}", "en").format(1234L));
    }

    /** 模板参数与展示用 JSON 并存时，转义的大括号应按原文输出。 */
    @Test
    public void formatsLiteralJsonAlongsideArgument() {
        I18nMessageTemplate template = new I18nMessageTemplate(
                "Parameter {0}: '{\"dataList\":[{\"text\":\"Yes\"}]}'", "en");
        assertEquals("Parameter source: {\"dataList\":[{\"text\":\"Yes\"}]}", template.format("source"));
    }

    /** 通过真实 MessageFormat 验证预处理后的最终展示文案。 */
    private String format(String pattern, Object... args) {
        return MessageFormat.format(I18nMessagePattern.normalize(pattern), args);
    }
}
