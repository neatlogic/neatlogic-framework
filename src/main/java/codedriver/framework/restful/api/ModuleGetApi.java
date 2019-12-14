package codedriver.framework.restful.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.dto.ModuleVo;
import codedriver.framework.restful.annotation.Description;
import codedriver.framework.restful.annotation.Input;
import codedriver.framework.restful.annotation.Output;
import codedriver.framework.restful.annotation.Param;
import codedriver.framework.restful.core.ApiComponentBase;
import codedriver.framework.service.UserService;

@Service
public class ModuleGetApi extends ApiComponentBase {
	@Autowired
	UserService userService;

	@Override
	public String getToken() {
		return "/module/get";
	}

	@Override
	public String getName() {
		return "获取租户激活模块接口";
	}

	@Override
	public String getConfig() {
		return null;
	}

	@Input({})
	@Output({@Param(explode = ModuleVo.class)})
	@Description(desc = "获取租户激活模块接口")
	@Override
	public Object myDoService(JSONObject jsonObj) throws Exception {
		return TenantContext.get().getActiveModuleList();
	}
}
