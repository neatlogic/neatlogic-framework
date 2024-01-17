/*
Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package neatlogic.module.framework.login.handler;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.common.ReturnJson;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.common.constvalue.ResponseCode;
import neatlogic.framework.common.util.TenantUtil;
import neatlogic.framework.dao.mapper.ThemeMapper;
import neatlogic.framework.dto.TenantVo;
import neatlogic.framework.dto.ThemeVo;
import neatlogic.framework.filter.core.ILoginAuthHandler;
import neatlogic.framework.filter.core.LoginAuthFactory;
import neatlogic.framework.service.TenantService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/tenant/")
public class TenantController {
    private final Logger logger = LoggerFactory.getLogger(TenantController.class);
    @Resource
    private TenantService tenantService;

    @Resource
    private ThemeMapper themeMapper;

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

            if(Config.LOGIN_AUTH_TYPE() != null) {
                ILoginAuthHandler loginAuth = LoginAuthFactory.getLoginAuth(Config.LOGIN_AUTH_TYPE());
                if(loginAuth != null) {
                    data.put("isNeedAuth", loginAuth.isNeedAuth());
                }
            }

            //单点登录
            if(StringUtils.isNotEmpty(Config.SSO_TICKET_KEY())) {
                data.put("ssoTicketKey", Config.SSO_TICKET_KEY());
            }
            ReturnJson.success(data, response);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }
}
