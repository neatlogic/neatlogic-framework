/*
 * Copyright (c)  2021 TechSure Co.,Ltd.  All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.module.framework.login.handler;

import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.common.ReturnJson;
import neatlogic.framework.common.util.TenantUtil;
import neatlogic.framework.dao.mapper.LicenseMapper;
import neatlogic.framework.dao.mapper.ThemeMapper;
import neatlogic.framework.dto.TenantVo;
import neatlogic.framework.dto.ThemeVo;
import neatlogic.framework.dto.license.LicenseVo;
import neatlogic.framework.service.TenantService;
import neatlogic.framework.util.TimeUtil;
import com.alibaba.fastjson.JSONObject;
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

    @Resource
    private LicenseMapper licenseMapper;

    @RequestMapping(value = "/check/{tenant}")
    public void checkTenant(@PathVariable("tenant") String tenant, HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            if (!TenantUtil.hasTenant(tenant)) {
                // 如果cache没有租户，尝试查询数据库，检查是否存在启用租户
                TenantVo tenantVo = tenantService.getTenantByUuid(tenant);
                if (tenantVo != null && tenantVo.getIsActive().equals(1)) {
                    TenantUtil.addTenant(tenant);
                } else {
                    response.setStatus(521);
                    ReturnJson.error("租户" + tenant + "不存在或已被禁用", response);
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
            //license
            JSONObject license = new JSONObject();
            LicenseVo licenseVo = TenantContext.get().getLicenseVo();
            boolean isValid = true;
            if (licenseVo == null) {
                isValid = false;
            } else {
                long diffTime = licenseVo.getExpireTime().getTime() - System.currentTimeMillis();
                license.put("expiredTime", TimeUtil.convertDateToString(licenseVo.getExpireTime(), TimeUtil.YYYY_MM_DD));
                license.put("stopServiceTime", TimeUtil.addDateByDay(licenseVo.getExpireTime(), licenseVo.getExpiredDay(), TimeUtil.YYYY_MM_DD));
                //license.put("willStopServiceDay", licenseVo.getCurrentExpireDay(diffTime));
                Long currentWillExpiredDay = licenseVo.getCurrentWillExpiredDay(diffTime);
                license.put("isWillExpired", currentWillExpiredDay != null && currentWillExpiredDay > 0);
                license.put("isExpired", diffTime < 0);
                if (licenseVo.isExpiredOutOfDay(diffTime)) {
                    isValid = false;
                }
            }
            license.put("isValid", isValid);
            data.put("license", license);
            ReturnJson.success(data, response);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }
}