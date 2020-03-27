package codedriver.framework.restful.api;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.dto.ModuleVo;
import codedriver.framework.restful.annotation.Description;
import codedriver.framework.restful.annotation.Input;
import codedriver.framework.restful.annotation.IsActived;
import codedriver.framework.restful.annotation.Output;
import codedriver.framework.restful.annotation.Param;
import codedriver.framework.restful.core.ApiComponentBase;
import codedriver.framework.service.UserService;

@IsActived
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
	@Output({ @Param(explode = ModuleVo.class) })
	@Description(desc = "获取租户激活模块接口")
	@Override
	public Object myDoService(JSONObject jsonObj) throws Exception {
		JSONArray resultArray = new JSONArray();
		Set<String> checkSet = new HashSet<>();
		for (ModuleVo moduleVo : TenantContext.get().getActiveModuleList()) {
			if (!checkSet.contains(moduleVo.getGroup())) {
				checkSet.add(moduleVo.getGroup());
				JSONObject returnObj = new JSONObject();
				returnObj.put("value", moduleVo.getGroup());
				returnObj.put("text", moduleVo.getGroupName());
				returnObj.put("sort", moduleVo.getGroupSort());
				resultArray.add(returnObj);
			}
		}
		Collections.sort(resultArray, new Comparator<Object>() {
			@Override
			public int compare(Object o1, Object o2) {
				try {
					JSONObject obj1 = (JSONObject) o1;
					JSONObject obj2 = (JSONObject) o2;
					return obj1.getIntValue("sort") - obj2.getIntValue("sort");
				} catch (Exception ex) {

				}
				return 0;
			}
		});
		return resultArray;
	}
}
