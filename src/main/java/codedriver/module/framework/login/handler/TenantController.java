/*
 * Copyright (c)  2021 TechSure Co.,Ltd.  All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.module.framework.login.handler;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.common.ReturnJson;
import codedriver.framework.common.util.TenantUtil;
import codedriver.framework.dao.mapper.ThemeMapper;
import codedriver.framework.dto.TenantVo;
import codedriver.framework.dto.ThemeVo;
import codedriver.framework.service.TenantService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/tenant/")
public class TenantController {
    @Autowired
    private TenantService tenantService;

    @Autowired
    private ThemeMapper themeMapper;

    @RequestMapping(value = "/check/{tenant}")
    public void checkTenant(@PathVariable("tenant") String tenant, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (!TenantUtil.hasTenant(tenant)) {
            // 如果cache没有租户，尝试查询数据库，检查是否存在启用租户
            TenantVo tenantVo = tenantService.getTenantByUuid(tenant);
            if (tenantVo != null && tenantVo.getIsActive().equals(1)) {
                TenantUtil.addTenant(tenant);
            } else {
                ReturnJson.error("租户" + tenant + "不存在或已被禁用", response);
            }
        }
        TenantContext.init().switchTenant(tenant);
        ThemeVo theme = themeMapper.getTheme();
        JSONObject data = new JSONObject();
        JSONObject themeConfig = new JSONObject();
        if (theme != null) {
            themeConfig = theme.getConfig();
        }
        data.put("themeConfig", themeConfig);
        ReturnJson.success(data, response);
    }
}
