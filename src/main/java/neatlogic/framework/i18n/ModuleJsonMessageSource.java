package neatlogic.framework.i18n;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.lang.Nullable;

import java.text.MessageFormat;
import java.util.Locale;

/**
 * 将统一翻译服务适配为 Spring MessageSource。
 */
public class ModuleJsonMessageSource implements MessageSource {
    private final I18nTranslator translator;

    /** 使用全局共享翻译服务创建消息源。 */
    public ModuleJsonMessageSource() {
        this(I18nRuntime.getTranslator());
    }

    /** 使用指定翻译服务创建消息源，供依赖注入和隔离测试使用。 */
    ModuleJsonMessageSource(I18nTranslator translator) {
        this.translator = translator;
    }

    /** 查找消息，缺失时返回调用方提供的默认文案。 */
    @Override
    @Nullable
    public String getMessage(String code, @Nullable Object[] args, @Nullable String defaultMessage, Locale locale) {
        Object[] resolvedArguments = resolveArguments(args, locale);
        String message = translator.translateExisting(locale, code, resolvedArguments);
        if (message != null) {
            return message;
        }
        return defaultMessage == null ? null : formatDefaultMessage(defaultMessage, resolvedArguments, locale);
    }

    /** 查找消息，缺失时保持 Spring 标准 NoSuchMessageException 语义。 */
    @Override
    public String getMessage(String code, @Nullable Object[] args, Locale locale) throws NoSuchMessageException {
        Object[] resolvedArguments = resolveArguments(args, locale);
        String message = translator.translateExisting(locale, code, resolvedArguments);
        if (message == null) {
            translator.reportMissing(locale, code);
            throw new NoSuchMessageException(code, locale);
        }
        return message;
    }

    /** 按候选 code 顺序解析 MessageSourceResolvable。 */
    @Override
    public String getMessage(MessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException {
        String[] codes = resolvable.getCodes();
        Object[] resolvedArguments = resolveArguments(resolvable.getArguments(), locale);
        if (codes != null) {
            for (String code : codes) {
                String message = translator.translateExisting(locale, code, resolvedArguments);
                if (message != null) {
                    return message;
                }
            }
        }
        String defaultMessage = resolvable.getDefaultMessage();
        if (defaultMessage != null) {
            return formatDefaultMessage(defaultMessage, resolvedArguments, locale);
        }
        String code = codes == null || codes.length == 0 ? "" : codes[codes.length - 1];
        translator.reportMissing(locale, code);
        throw new NoSuchMessageException(code, locale);
    }

    /** 解析参数中的嵌套消息，仅在需要替换时复制调用方数组。 */
    @Nullable
    private Object[] resolveArguments(@Nullable Object[] args, Locale locale) {
        if (args == null || args.length == 0) {
            return args;
        }
        Object[] resolvedArguments = null;
        for (int index = 0; index < args.length; index++) {
            if (args[index] instanceof MessageSourceResolvable resolvable) {
                if (resolvedArguments == null) {
                    resolvedArguments = args.clone();
                }
                resolvedArguments[index] = getMessage(resolvable, locale);
            }
        }
        return resolvedArguments == null ? args : resolvedArguments;
    }

    /** 默认文案仅在存在参数时使用 MessageFormat，保持无参数原文语义。 */
    private String formatDefaultMessage(String defaultMessage, @Nullable Object[] args, Locale locale) {
        if (args == null || args.length == 0) {
            return defaultMessage;
        }
        Locale resolvedLocale = I18nLocaleResolver.resolveLocale(locale);
        return new MessageFormat(I18nMessagePattern.normalize(defaultMessage), resolvedLocale).format(args);
    }
}
