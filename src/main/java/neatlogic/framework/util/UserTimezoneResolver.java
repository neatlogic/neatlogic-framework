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

package neatlogic.framework.util;

import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.config.ConfigManager;
import neatlogic.framework.config.FrameworkTenantConfig;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.DateTimeException;
import java.time.ZoneOffset;

/** 在上下文初始化阶段解析固定 UTC 偏移量。 */
public final class UserTimezoneResolver {
    private static final Logger logger = LoggerFactory.getLogger(UserTimezoneResolver.class);

    /** 仅提供无状态的时区解析方法。 */
    private UserTimezoneResolver() {
    }

    /** 浏览器时区有效时优先使用，否则使用当前租户默认时区。 */
    public static String getRequestTimezone(HttpServletRequest request) {
        Cookie[] cookies = request == null ? null : request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (!"neatlogic_timezone".equals(cookie.getName())) {
                    continue;
                }
                try {
                    // 保留 Cookie 中的原始正号，并兼容 encodeURIComponent 产生的编码值。
                    String value = cookie.getValue();
                    ZoneOffset offset = parse(value == null ? null : URLDecoder.decode(
                            value.replace("+", "%2B"), StandardCharsets.UTF_8),
                            $.t("framework.usertimezoneresolver.source.request"));
                    if (offset != null) {
                        return format(offset);
                    }
                } catch (IllegalArgumentException ex) {
                    // 不记录未经校验的 Cookie 内容，编码失败时直接使用租户默认值。
                    logger.error($.t("framework.usertimezoneresolver.invalidcookieencoding",
                            ex.getClass().getSimpleName()));
                }
                break;
            }
        }
        return getDefaultTimezone();
    }

    /** 返回当前租户配置的默认时区；没有租户或配置不可用时使用枚举默认值。 */
    public static String getDefaultTimezone() {
        return format(resolve(null));
    }

    /** 校验已有时区；缺失或无效时依次回退租户配置和枚举默认值。 */
    public static ZoneOffset resolve(String offset) {
        ZoneOffset explicitOffset = parse(offset, $.t("framework.usertimezoneresolver.source.explicit"));
        if (explicitOffset != null) {
            return explicitOffset;
        }
        TenantContext tenantContext = TenantContext.get();
        if (StringUtils.isNotBlank(tenantContext.getTenantUuid())) {
            try {
                ZoneOffset configuredOffset = parse(
                        ConfigManager.getConfig(FrameworkTenantConfig.TENANT_DEFAULT_TIMEZONE),
                        $.t("framework.usertimezoneresolver.source.tenantdefault"));
                if (configuredOffset != null) {
                    return configuredOffset;
                }
            } catch (Exception ex) {
                // 配置读取失败不能阻断上下文初始化，保留异常用于定位租户配置问题。
                logger.error($.t("framework.usertimezoneresolver.configreadfailed",
                        tenantContext.getTenantUuid()), ex);
            }
        }
        return ZoneOffset.of(FrameworkTenantConfig.TENANT_DEFAULT_TIMEZONE.getValue());
    }

    /** 输出数据库和用户上下文统一使用的 ±HH:mm 格式。 */
    private static String format(ZoneOffset offset) {
        return ZoneOffset.UTC.equals(offset) ? "+00:00" : offset.getId();
    }

    /** 使用 JDK 校验固定偏移量，平台上下文只接受分钟精度。 */
    private static ZoneOffset parse(String value, String source) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        try {
            ZoneOffset offset = ZoneOffset.of(value.trim());
            if (offset.getTotalSeconds() % 60 == 0) {
                return offset;
            }
            logger.error($.t("framework.usertimezoneresolver.secondprecisionoffset", source));
        } catch (DateTimeException ex) {
            logger.error($.t("framework.usertimezoneresolver.invalidoffset",
                    source, ex.getClass().getSimpleName()));
        }
        return null;
    }
}
