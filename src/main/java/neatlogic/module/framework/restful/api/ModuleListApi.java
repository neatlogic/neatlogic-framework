/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

package neatlogic.module.framework.restful.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.dao.mapper.UserMapper;
import neatlogic.framework.dto.AuthenticationInfoVo;
import neatlogic.framework.dto.UserAuthVo;
import neatlogic.framework.dto.module.ModuleVo;
import neatlogic.framework.restful.annotation.*;
import neatlogic.framework.restful.constvalue.OperationTypeEnum;
import neatlogic.framework.restful.core.privateapi.PrivateApiComponentBase;
import neatlogic.framework.util.$;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@OperationType(type = OperationTypeEnum.SEARCH)
public class ModuleListApi extends PrivateApiComponentBase {
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

    @Input({})
    @Output({
            @Param(name = "value", type = ApiParamType.STRING, desc = "模块"),
            @Param(name = "text", type = ApiParamType.STRING, desc = "模块名")
    })
    @Description(desc = "获取租户激活模块接口")
    @Override
    public Object myDoService(JSONObject jsonObj) throws Exception {
        JSONArray resultArray = new JSONArray();
        Set<String> authGroupSet = new HashSet<String>();
        //获取用户权限
        AuthenticationInfoVo authenticationInfoVo = UserContext.get().getAuthenticationInfoVo();
        List<UserAuthVo> userAuthList = userMapper.searchUserAllAuthByUserAuth(authenticationInfoVo);
        for (UserAuthVo userAuth : userAuthList) {
            authGroupSet.add(userAuth.getAuthGroup());
        }
        Set<String> checkSet = new HashSet<>();
        for (ModuleVo moduleVo : TenantContext.get().getActiveModuleList()) {
            if (authGroupSet.contains(moduleVo.getGroup()) && !checkSet.contains(moduleVo.getGroup())) {
                checkSet.add(moduleVo.getGroup());
                JSONObject returnObj = new JSONObject();
                returnObj.put("value", moduleVo.getGroup());
                returnObj.put("text", $.t(moduleVo.getGroupName()));
                returnObj.put("sort", moduleVo.getGroupSort());
                resultArray.add(returnObj);
            }
        }
        resultArray.sort(Comparator.comparing(obj -> ((JSONObject) obj).getInteger("sort")));

        return resultArray;
    }
}
