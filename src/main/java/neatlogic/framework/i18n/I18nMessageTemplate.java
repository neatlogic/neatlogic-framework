package neatlogic.framework.i18n;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 保存已校验的消息模板，并负责线程安全的参数格式化。
 */
final class I18nMessageTemplate {
    private static final Pattern ARGUMENT_START = Pattern.compile("\\{([0-9]+)");

    private final String message;
    private final MessageFormat prototype;
    private final Set<Integer> argumentIndexes;
    private final boolean hasExplicitFormat;

    /** 编译消息模板；出现编号参数起始标记时必须符合完整 MessageFormat 语法。 */
    I18nMessageTemplate(String message, String language) {
        this.message = message;
        this.argumentIndexes = extractArgumentIndexes(message);
        if (argumentIndexes.isEmpty()) {
            this.prototype = null;
            this.hasExplicitFormat = false;
        } else {
            this.prototype = new MessageFormat(I18nMessagePattern.normalize(message), Locale.forLanguageTag(language));
            this.hasExplicitFormat = Arrays.stream(prototype.getFormats()).anyMatch(format -> format != null);
        }
    }

    /** 返回模板引用的参数索引。 */
    Set<Integer> getArgumentIndexes() {
        return argumentIndexes;
    }

    /** 无参数调用返回资源原文；有参数时按模板格式化。 */
    String format(Object... args) {
        if (args == null || args.length == 0 || prototype == null) {
            return message;
        }
        Object[] normalizedArgs = args;
        if (!hasExplicitFormat) {
            normalizedArgs = Arrays.copyOf(args, args.length);
            for (Integer index : argumentIndexes) {
                if (index >= normalizedArgs.length) {
                    continue;
                }
                Object value = normalizedArgs[index];
                normalizedArgs[index] = value == null ? "" : value.toString();
            }
        }
        return ((MessageFormat) prototype.clone()).format(normalizedArgs);
    }

    /** 从模板中提取参数索引，供镜像校验和普通参数归一化使用。 */
    static Set<Integer> extractArgumentIndexes(String message) {
        if (message == null || message.isEmpty()) {
            return Collections.emptySet();
        }
        Set<Integer> indexes = new LinkedHashSet<>();
        Matcher matcher = ARGUMENT_START.matcher(message);
        while (matcher.find()) {
            indexes.add(Integer.parseInt(matcher.group(1)));
        }
        return Collections.unmodifiableSet(indexes);
    }
}
