/*Copyright (C) $today.year  深圳极向量科技有限公司 All Rights Reserved.

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

package neatlogic.module.framework.login.handler;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.common.ReturnJson;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.common.constvalue.ResponseCode;
import neatlogic.framework.common.util.TenantUtil;
import neatlogic.framework.config.ConfigManager;
import neatlogic.framework.config.FrameworkTenantConfig;
import neatlogic.framework.dao.mapper.ThemeMapper;
import neatlogic.framework.dto.TenantVo;
import neatlogic.framework.dto.ThemeVo;
import neatlogic.framework.filter.core.ILoginAuthHandler;
import neatlogic.framework.filter.core.LoginAuthFactory;
import neatlogic.framework.service.TenantService;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Controller
@RequestMapping("/tenant/")
public class TenantController {
    private final Logger logger = LoggerFactory.getLogger(TenantController.class);
    @Resource
    private TenantService tenantService;
    private final static Set<String> moduleSet = new HashSet<>();
    private boolean isLoad = false;
    @Resource
    private ThemeMapper themeMapper;

    private void getCommercialModule() {
        Reflections reflections = new Reflections("neatlogic");
        Set<Class<? extends InstantiationAwareBeanPostProcessor>> authClass = reflections.getSubTypesOf(InstantiationAwareBeanPostProcessor.class);
        for (Class<? extends InstantiationAwareBeanPostProcessor> c : authClass) {
            try {
                if (!c.getSimpleName().endsWith("AuthBean")) {
                    continue;
                }
                Field field = c.getDeclaredField("moduleSet");
                field.setAccessible(true);
                Object value = field.get(null);
                moduleSet.addAll((Collection<? extends String>) value);
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }
        }
        isLoad = true;
    }

    @RequestMapping(value = "/check/{tenant}")
    public void checkTenant(@PathVariable("tenant") String tenant, HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            if (!TenantUtil.hasTenant(tenant)) {
                // 如果cache没有租户，尝试查询数据库，检查是否存在启用租户
                TenantVo tenantVo = tenantService.getTenantByUuid(tenant);
                if (tenantVo != null && tenantVo.getIsActive().equals(1)) {
                    TenantUtil.addTenant(tenant);
                } else {
                    response.setStatus(ResponseCode.TENANT_NOTFOUND.getCode());
                    ReturnJson.error(ResponseCode.TENANT_NOTFOUND.getMessage(tenant), response);
                    return;//没有租户，后续代码无需执行
                }
            }
            JSONObject data = new JSONObject();
            //theme
            TenantContext.init().switchTenant(tenant);
            ThemeVo theme = themeMapper.getTheme();
            JSONObject themeConfig = new JSONObject();
            if (theme != null) {
                themeConfig = theme.getConfig();
            }
            data.put("themeConfig", themeConfig);
            //登录插件
            data.put("authType", Config.LOGIN_AUTH_TYPE());
            data.put("encrypt", Config.LOGIN_AUTH_PASSWORD_ENCRYPT());
            if (!isLoad) {
                getCommercialModule();
            }
            data.put("commercialModuleSet", moduleSet);

            if (Config.LOGIN_AUTH_TYPE() != null) {
                ILoginAuthHandler loginAuth = LoginAuthFactory.getLoginAuth(Config.LOGIN_AUTH_TYPE());
                if (loginAuth != null) {
                    data.put("isNeedAuth", loginAuth.isNeedAuth());
                }
            }

            //单点登录
            if (StringUtils.isNotEmpty(Config.SSO_TICKET_KEY())) {
                data.put("ssoTicketKey", Config.SSO_TICKET_KEY());
            }
            // 是否允许移动端下载附件
            data.put("mobileFileDownloadEnabled", ConfigManager.getConfig(FrameworkTenantConfig.MOBILE_FILE_DOWNLOAD_ENABLED));
            // Chrome浏览器最低版本，低于该设置版本会跳转至浏览器版本不兼容页面，默认95
            data.put("minimumChromeBrowserVersion", ConfigManager.getConfig(FrameworkTenantConfig.MINIMUM_CHROME_BROWSER_VERSION));
            // Firefox浏览器最低版本，低于该设置版本会跳转至浏览器版本不兼容页面，默认0
            data.put("minimumFirefoxBrowserVersion", ConfigManager.getConfig(FrameworkTenantConfig.MINIMUM_FIREFOX_BROWSER_VERSION));
            ReturnJson.success(data, response);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }
}
