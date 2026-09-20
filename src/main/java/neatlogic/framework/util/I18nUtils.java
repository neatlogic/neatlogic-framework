/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 *
 */
package neatlogic.framework.util;

import neatlogic.framework.i18n.I18nRuntime;
import neatlogic.framework.i18n.I18nTranslator;

import java.util.Locale;

/**
 * 兼容历史静态调用的国际化门面；新代码优先注入 I18nTranslator。
 */
public class I18nUtils {
    private static final I18nTranslator TRANSLATOR = I18nRuntime.getTranslator();

    /** 使用当前请求语言获取翻译。 */
    public static String getMessage(String key, Object... args) {
        return TRANSLATOR.translate(key, args);
    }

    /** 使用当前请求语言获取无参数翻译。 */
    public static String getMessage(String key) {
        return TRANSLATOR.translate(key);
    }

    /** Spring 启动前按默认中文获取无参数翻译。 */
    public static String getStaticMessage(String key) {
        return TRANSLATOR.translate(Locale.CHINESE, key);
    }

    /** Spring 启动前按指定语言获取无参数翻译。 */
    public static String getStaticMessage(Locale locale, String key) {
        return TRANSLATOR.translate(locale, key);
    }

    /** Spring 启动前按默认中文获取带参数翻译。 */
    public static String getStaticMessage(String key, Object... args) {
        return TRANSLATOR.translate(Locale.CHINESE, key, args);
    }

    /** Spring 启动前按指定语言获取带参数翻译。 */
    public static String getStaticMessage(Locale locale, String key, Object... args) {
        return TRANSLATOR.translate(locale, key, args);
    }
}
