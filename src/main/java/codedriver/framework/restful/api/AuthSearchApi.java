package codedriver.framework.restful.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.auth.core.AuthBase;
import codedriver.framework.auth.core.AuthFactory;
import codedriver.framework.common.constvalue.ApiParamType;
import codedriver.framework.common.util.ModuleUtil;
import codedriver.framework.dto.AuthGroupVo;
import codedriver.framework.dto.AuthVo;
import codedriver.framework.restful.annotation.Description;
import codedriver.framework.restful.annotation.Input;
import codedriver.framework.restful.annotation.Output;
import codedriver.framework.restful.annotation.Param;
import codedriver.framework.restful.core.ApiComponentBase;

@Service
public class AuthSearchApi extends ApiComponentBase {

    @Override
    public String getToken() {
        return "auth/search";
    }

    @Override
    public String getName() {
        return "权限列表查询接口";
    }

    @Override
    public String getConfig() {
        return null;
    }

    @Input({
    	@Param( name = "groupName", type = ApiParamType.STRING, desc = "权限组名")
    })
    @Output({
        @Param( name = "authGroupList", type = ApiParamType.JSONARRAY, desc = "权限列表组集合", explode = AuthGroupVo[].class)
    })
    @Description(desc = "权限列表查询接口")
    @Override
    public Object myDoService(JSONObject jsonObj) throws Exception {
        JSONObject returnObj = new JSONObject();
        String groupName = jsonObj.getString("groupName");
        List<AuthGroupVo> authGroupVoList = new ArrayList<>();
        Map<String, List<AuthBase>> authGroupMap = AuthFactory.getAuthGroupMap();
        for (Map.Entry<String, List<AuthBase>> entry : authGroupMap.entrySet()){
            String authGroupName = entry.getKey();
            if(!TenantContext.get().getActiveModuleMap().containsKey(authGroupName) || (groupName != null && !groupName.equalsIgnoreCase(authGroupName))) {
            	continue;
            }
            AuthGroupVo authGroupVo = new AuthGroupVo();
            authGroupVo.setName(authGroupName);
            authGroupVo.setDisplayName(ModuleUtil.getModuleGroup(authGroupName).getGroupName());
            List<AuthBase> authList = authGroupMap.get(authGroupName);
            if (authList != null && authList.size() > 0){
                List<AuthVo> authArray = new ArrayList<>();
                for (AuthBase authBase : authList){
                    AuthVo authVo = new AuthVo();
                    authVo.setName(authBase.getAuthName());
                    authVo.setDisplayName(authBase.getAuthDisplayName());
                    authArray.add(authVo);
                }
                authGroupVo.setAuthVoList(authArray);
            }
            authGroupVoList.add(authGroupVo);
        }
        returnObj.put("authGroupList", authGroupVoList);
        return returnObj;
    }
}
