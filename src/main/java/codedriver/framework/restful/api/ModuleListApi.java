package codedriver.framework.restful.api;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.asynchronization.threadlocal.UserContext;
import codedriver.framework.common.constvalue.ApiParamType;
import codedriver.framework.dao.mapper.UserMapper;
import codedriver.framework.dto.ModuleVo;
import codedriver.framework.dto.UserAuthVo;
import codedriver.framework.restful.annotation.Description;
import codedriver.framework.restful.annotation.Input;
import codedriver.framework.restful.annotation.IsActived;
import codedriver.framework.restful.annotation.Output;
import codedriver.framework.restful.annotation.Param;
import codedriver.framework.restful.core.ApiComponentBase;

@IsActived
@Service
public class ModuleListApi extends ApiComponentBase {
	@Autowired
	UserMapper userMapper;

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

	@Input({@Param( name = "includeList", type = ApiParamType.JSONARRAY, desc = "白名单")})
	@Output({
		@Param( name = "value", type = ApiParamType.STRING, desc = "模块"),
		@Param( name = "text", type = ApiParamType.STRING, desc = "模块名") 
		})
	@Description(desc = "获取租户激活模块接口")
	@Override
	public Object myDoService(JSONObject jsonObj) throws Exception {
		JSONArray resultArray = new JSONArray();
		 Set<String> authGroupSet = new HashSet<String>();
        //获取用户权限
        List<UserAuthVo>  userAuthList = userMapper.searchUserAllAuthByUserAuth(new UserAuthVo(UserContext.get().getUserUuid()));
        for(UserAuthVo userAuth:userAuthList) {
        	authGroupSet.add(userAuth.getAuthGroup());
        }
		Set<String> checkSet = new HashSet<>();
		for (ModuleVo moduleVo : TenantContext.get().getActiveModuleList()) {
			if (authGroupSet.contains(moduleVo.getGroup())&&!checkSet.contains(moduleVo.getGroup())) {
				checkSet.add(moduleVo.getGroup());
				JSONObject returnObj = new JSONObject();
				returnObj.put("value", moduleVo.getGroup());
				returnObj.put("text", moduleVo.getGroupName());
				returnObj.put("sort", moduleVo.getGroupSort());
				resultArray.add(returnObj);
			}
		}
		JSONArray includeList = jsonObj.getJSONArray("includeList");
		if(CollectionUtils.isNotEmpty(includeList)&&includeList.contains("dashboard")) {
			//添加仪表板 TODO 等前端完成迁移，则删除此逻辑
			JSONObject returnObj = new JSONObject();
			returnObj.put("value", "dashboard");
			returnObj.put("text", "仪表板");
			returnObj.put("sort", 0);
			resultArray.add(returnObj);
		}
		resultArray.sort(Comparator.comparing(obj-> ((JSONObject) obj).getInteger("sort")));
		
		return resultArray;
	}
}
