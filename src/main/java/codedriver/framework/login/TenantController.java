package codedriver.framework.login;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import codedriver.framework.common.ReturnJson;
import codedriver.framework.common.util.TenantUtil;

@Controller
@RequestMapping("/tenant/")
public class TenantController {

	@RequestMapping(value = "/check/{tenant}")
	public void checkTenant(@PathVariable("tenant") String tenant, HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (!TenantUtil.hasTenant(tenant)) {
			ReturnJson.error("租户" + tenant + "不存在或已被禁用", response);
		}
		ReturnJson.success(response);
	}
}
