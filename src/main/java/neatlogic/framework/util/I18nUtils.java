/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package neatlogic.framework.util;

import neatlogic.framework.asynchronization.threadlocal.RequestContext;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.MessageSourceAccessor;

import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
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
        MessageSourceAccessor messageSource = SpringContextUtil.getBean(MessageSourceAccessor.class);
        Locale locale = RequestContext.get() != null ? RequestContext.get().getLocale() : Locale.getDefault();
        String value = key;
        try {
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
        MessageSourceAccessor messageSource = SpringContextUtil.getBean(MessageSourceAccessor.class);
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
        // 获得资源文件
        ResourceBundle rb = ResourceBundle.getBundle("i18n/language", locale);
        // 获得相应的key值
        String value = key;
        try {
            value = new String(rb.getString(key).getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        } catch (Exception ignored) {
        }
        return MessageFormat.format(value, args);
    }


}
