/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

package neatlogic.framework.util;

import neatlogic.framework.asynchronization.threadlocal.RequestContext;
import neatlogic.framework.i18n.JsonResourceBundleControl;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.MessageSourceAccessor;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;

public class I18nUtils {

    /**
     * 获取翻译
     * 使用场景：系统启动后执行的翻译
     * @param key 键
     * @param args 值参数
     * @return 翻译值
     */
    public static String getMessage(String key, Object... args) {
        MessageSourceAccessor messageSource = SpringContextUtil.getBean("messageSourceAccessor",MessageSourceAccessor.class);
        Locale locale = RequestContext.get() != null ? RequestContext.get().getLocale() : Locale.getDefault();
        String value = key;
        try {
            args = Arrays.stream(args).map(String::valueOf).toArray(); //解决Long类型参数被格式化问题
            value = messageSource.getMessage(key, args, locale);
        } catch (NoSuchMessageException ignored) {
        }
        return value;
    }

    /**
     * 获取翻译
     * 使用场景：系统启动后执行的翻译
     * @param key 键
     * @return 翻译值
     */
    public static String getMessage(String key) {
        MessageSourceAccessor messageSource = SpringContextUtil.getBean("messageSourceAccessor",MessageSourceAccessor.class);
        Locale locale = RequestContext.get() != null ? RequestContext.get().getLocale() : Locale.getDefault();
        String value = key;
        try {
            value = messageSource.getMessage(key, locale);
        } catch (NoSuchMessageException ignored) {
        }
        return value;
    }

    /**
     * 获取翻译
     * 只有spring 还没有加载MessageSourceAccessor bean 才使用方法
     * @param key 键
     * @return 翻译值
     */
    public static String getStaticMessage(String key) {
        return getStaticMessage(Locale.CHINESE, key);
    }

    /**
     * 获取翻译
     * 只有spring 还没有加载MessageSourceAccessor bean 才使用方法
     * @param locale 目标翻译语言 如 Locale.CHINESE
     * @param key 键
     * @return 翻译值
     */
    public static String getStaticMessage(Locale locale, String key) {
        return getStaticMessage(locale, key, CollectionUtils.EMPTY_COLLECTION);
    }

    /**
     * 获取翻译
     * 只有spring 还没有加载MessageSourceAccessor bean 才使用方法
     * @param key 键
     * @param args 值参数
     * @return 翻译值
     */
    public static String getStaticMessage(String key, Object... args) {
        return getStaticMessage(Locale.CHINESE, key, args);
    }

    /**
     * 获取翻译
     * 只有spring 还没有加载MessageSourceAccessor bean 才使用方法
     * @param locale 目标翻译语言 如 Locale.CHINESE
     * @param key 键
     * @param args 值参数
     * @return 翻译值
     */
    public static <E> String getStaticMessage(Locale locale, String key, Object... args) {
        // 设置定制的语言国家代码
        if (locale == null) {
            locale = Locale.CHINESE;
        }
        Locale.setDefault(locale);
        ResourceBundle bundle = ResourceBundle.getBundle("i18n/language", new JsonResourceBundleControl());
        String value;
        try {
            value = bundle.getString(key);
            args = Arrays.stream(args).map(String::valueOf).toArray(); //解决Long类型参数被格式化问题
        } catch (Exception ignored) {
            value = key;
        }
        return MessageFormat.format(value, args);
    }
}
