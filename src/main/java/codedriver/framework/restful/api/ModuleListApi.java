package codedriver.framework.restful.api;

import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import codedriver.framework.common.constvalue.ModuleEnum;
import codedriver.framework.dto.ModuleVo;
import codedriver.framework.restful.annotation.Description;
import codedriver.framework.restful.annotation.Input;
import codedriver.framework.restful.annotation.IsActive;
import codedriver.framework.restful.annotation.Output;
import codedriver.framework.restful.annotation.Param;
import codedriver.framework.restful.core.ApiComponentBase;
import codedriver.framework.service.UserService;

@IsActive
@Service
public class ModuleListApi extends ApiComponentBase {
	@Autowired
	UserService userService;

	@Override
	public String getToken() {
		return "/module/list";
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
		JSONArray resultArray = new JSONArray();
		Map<String,String> activeModuleMap = ModuleEnum.getActiveModule();
		for(Entry<String, String> module: activeModuleMap.entrySet()) {
			JSONObject resultJson = new JSONObject();
			resultJson.put("value", module.getKey());
			resultJson.put("text", module.getValue());
			resultArray.add(resultJson);
		}
		return resultArray;
	}
}
