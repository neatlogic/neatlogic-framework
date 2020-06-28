package codedriver.framework.login;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import codedriver.framework.common.ReturnJson;
import codedriver.framework.common.util.TenantUtil;
import codedriver.framework.dto.TenantVo;
import codedriver.framework.service.TenantService;

@Controller
@RequestMapping("/tenant/")
public class TenantController {
	@Autowired
	private TenantService tenantService;

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
		ReturnJson.success(response);
	}
}
