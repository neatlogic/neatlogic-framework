/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */

package neatlogic.framework.util;

import neatlogic.framework.asynchronization.threadlocal.RequestContext;
import neatlogic.framework.i18n.JsonResourceBundleControl;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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
     *
     * @param key  键
     * @param args 值参数
     * @return 翻译值
     */
    public static String getMessage(String key, Object... args) {
        MessageSourceAccessor messageSource = SpringContextUtil.getBean("messageSourceAccessor", MessageSourceAccessor.class);
        Locale locale = RequestContext.get() != null ? RequestContext.get().getLocale() : Locale.getDefault();
        String value = key;
        try {
            if (args != null) {
                args = Arrays.stream(args).map(arg -> arg == null ? StringUtils.EMPTY : arg.toString()).toArray(); //解决Long类型参数被格式化问题
            }
            value = messageSource.getMessage(key, args, locale);
        } catch (NoSuchMessageException ignored) {
        }
        return value;
    }

    /**
     * 获取翻译
     * 使用场景：系统启动后执行的翻译
     *
     * @param key 键
     * @return 翻译值
     */
    public static String getMessage(String key) {
        MessageSourceAccessor messageSource = SpringContextUtil.getBean("messageSourceAccessor", MessageSourceAccessor.class);
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
     *
     * @param key 键
     * @return 翻译值
     */
    public static String getStaticMessage(String key) {
        return getStaticMessage(Locale.CHINESE, key);
    }

    /**
     * 获取翻译
     * 只有spring 还没有加载MessageSourceAccessor bean 才使用方法
     *
     * @param locale 目标翻译语言 如 Locale.CHINESE
     * @param key    键
     * @return 翻译值
     */
    public static String getStaticMessage(Locale locale, String key) {
        return getStaticMessage(locale, key, CollectionUtils.EMPTY_COLLECTION);
    }

    /**
     * 获取翻译
     * 只有spring 还没有加载MessageSourceAccessor bean 才使用方法
     *
     * @param key  键
     * @param args 值参数
     * @return 翻译值
     */
    public static String getStaticMessage(String key, Object... args) {
        return getStaticMessage(Locale.CHINESE, key, args);
    }

    /**
     * 获取翻译
     * 只有spring 还没有加载MessageSourceAccessor bean 才使用方法
     *
     * @param locale 目标翻译语言 如 Locale.CHINESE
     * @param key    键
     * @param args   值参数
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
            if (args != null) {
                args = Arrays.stream(args).map(arg -> arg == null ? StringUtils.EMPTY : arg.toString()).toArray(); //解决Long类型参数被格式化问题
            }
        } catch (Exception ignored) {
            value = key;
        }
        return MessageFormat.format(value, args);
    }
}
