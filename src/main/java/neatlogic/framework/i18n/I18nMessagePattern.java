package neatlogic.framework.i18n;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 为普通编号占位符兼容显示用单引号，复杂模板继续遵循 MessageFormat 原生语义。
 */
public final class I18nMessagePattern {
    private static final Pattern INDEXED_ARGUMENT = Pattern.compile("\\{[0-9]+\\}");

    /** 工具类不允许实例化。 */
    private I18nMessagePattern() {
    }

    /**
     * 将普通模板中落单的单引号补为转义对；已有转义对保持不变，重复处理结果一致。
     * 仅处理含编号占位符且无其他大括号的模板，不修改参数值或原始语言包。
     *
     * @param pattern 原始翻译模板
     * @return 可交给 MessageFormat 解析的模板，不符合兼容条件时返回原值
     */
    public static String normalize(String pattern) {
        if (pattern == null || pattern.indexOf('\'') < 0) {
            return pattern;
        }
        Matcher matcher = INDEXED_ARGUMENT.matcher(pattern);
        if (!matcher.find()) {
            return pattern;
        }
        String remainder = matcher.replaceAll("");
        // 高级格式、JSON 和其他大括号表达式可能依赖原生转义，不能自动调整。
        if (remainder.indexOf('{') >= 0 || remainder.indexOf('}') >= 0) {
            return pattern;
        }
        StringBuilder result = new StringBuilder(pattern.length());
        for (int i = 0; i < pattern.length();) {
            char current = pattern.charAt(i);
            if (current != '\'') {
                result.append(current);
                i++;
                continue;
            }
            int start = i;
            while (i < pattern.length() && pattern.charAt(i) == '\'') {
                result.append('\'');
                i++;
            }
            // 保留已有转义对，仅补齐奇数连续单引号的最后一个，保证幂等。
            if ((i - start) % 2 != 0) {
                result.append('\'');
            }
        }
        return result.toString();
    }
}
