/*
 * Copyright (C) 2026  深圳极向量科技有限公司 All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package neatlogic.framework.globallock;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.util.SpringContextUtil;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import java.lang.reflect.Field;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

/** 为隔离测试加载实际状态语言包，不启动完整业务容器。 */
final class GlobalLockI18nFixture implements AutoCloseable {
    private final Field contextField;
    private final Object previous;
    private final Locale previousLocale;
    private final StaticApplicationContext context = new StaticApplicationContext();

    /** 保留原有上下文并注册生产语言包中的全局锁翻译。 */
    GlobalLockI18nFixture() throws Exception {
        contextField = SpringContextUtil.class.getDeclaredField("ctx");
        contextField.setAccessible(true); previous = contextField.get(null);
        previousLocale = Locale.getDefault();
        StaticMessageSource messages = new StaticMessageSource();
        for (String language : new String[]{"zh", "en"}) {
            JSONObject json = JSONObject.parseObject(new String(Files.readAllBytes(Paths.get("../neatlogic-resources/localconfig/i18n/language_" + language + ".json")), StandardCharsets.UTF_8));
            registerGlobalLockMessages(messages, json, "", new Locale(language));
            // 公共参数异常使用嵌套语言包，按实际路径注册，不能按平铺 key 查找。
            JSONObject rules = json.getJSONObject("nfet").getJSONObject("paramirregularexception").getJSONObject("paramirregularexception");
            for (String key : rules.keySet()) {
                messages.addMessage("nfet.paramirregularexception.paramirregularexception." + key, new Locale(language), rules.getString(key));
            }
        }
        context.getBeanFactory().registerSingleton("messageSourceAccessor", new MessageSourceAccessor(messages));
        new SpringContextUtil().setApplicationContext(context);
        Locale.setDefault(Locale.CHINESE);
    }

    /** 递归展开嵌套语言包，同时兼容历史平铺格式，只注册全局锁测试需要的文案。 */
    private void registerGlobalLockMessages(StaticMessageSource messages, JSONObject json, String prefix, Locale locale) {
        for (String key : json.keySet()) {
            Object value = json.get(key);
            String messageKey = prefix.isEmpty() ? key : prefix + "." + key;
            if (value instanceof JSONObject) {
                registerGlobalLockMessages(messages, (JSONObject) value, messageKey, locale);
            } else if (messageKey.startsWith("globallock.") && value instanceof String) {
                messages.addMessage(messageKey, locale, (String) value);
            }
        }
    }

    /** 恢复全局上下文和默认语言，避免影响其他测试。 */
    @Override public void close() throws Exception {
        contextField.set(null, previous); Locale.setDefault(previousLocale); context.close();
    }
}
