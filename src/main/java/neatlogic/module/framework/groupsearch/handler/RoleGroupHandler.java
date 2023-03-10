/*
Copyright(c) $today.year NeatLogic Co., Ltd. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. 
 */

package neatlogic.module.framework.groupsearch.handler;

import neatlogic.framework.common.constvalue.GroupSearch;
import neatlogic.framework.dao.mapper.RoleMapper;
import neatlogic.framework.dto.RoleVo;
import neatlogic.framework.restful.groupsearch.core.IGroupSearchHandler;
import neatlogic.framework.service.RoleService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class RoleGroupHandler implements IGroupSearchHandler {
    @Autowired
    private RoleMapper roleMapper;

    @Resource
    private RoleService roleService;

    @Override
    public String getName() {
        return GroupSearch.ROLE.getValue();
    }

    @Override
    public String getHeader() {
        return getName() + "#";
    }


    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> search(JSONObject jsonObj) {
        //总显示选项个数
        Integer total = jsonObj.getInteger("total");
        if (total == null) {
            total = 18;
        }
        List<RoleVo> roleList = new ArrayList<RoleVo>();
        RoleVo roleVo = new RoleVo();
        roleVo.setNeedPage(true);
        roleVo.setPageSize(total);
        roleVo.setCurrentPage(1);
        roleVo.setKeyword(jsonObj.getString("keyword"));
        //如果存在rangeList 则需要过滤option
        List<Object> rangeList = jsonObj.getJSONArray("rangeList");
        if (CollectionUtils.isNotEmpty(rangeList)) {
            List<String> roleUuidList = new ArrayList<>();
            rangeList.forEach(r -> {
                if (r.toString().startsWith(GroupSearch.ROLE.getValuePlugin())) {
                    roleUuidList.add(GroupSearch.removePrefix(r.toString()));
                }
            });
            if (CollectionUtils.isEmpty(roleUuidList)) {//如果rangList不为空 但roleUuidList为空则 无需返回角色
                roleUuidList.add("role#no_role");
            }
            roleVo.setRoleUuidList(roleUuidList);
        }
        roleList = roleMapper.searchRole(roleVo);
        roleService.setRoleTeamCountAndRoleUserCount(roleList);
        return (List<T>) roleList;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> reload(JSONObject jsonObj) {
        List<RoleVo> roleList = new ArrayList<RoleVo>();
        List<String> roleUuidList = new ArrayList<String>();
        for (Object value : jsonObj.getJSONArray("valueList")) {
            if (value.toString().startsWith(getHeader())) {
                roleUuidList.add(value.toString().replace(getHeader(), ""));
            }
        }
        if (roleUuidList.size() > 0) {
            roleList = roleMapper.getRoleByUuidList(roleUuidList);
        }
        return (List<T>) roleList;
    }

    @Override
    public <T> JSONObject repack(List<T> roleList) {
        JSONObject roleObj = new JSONObject();
        roleObj.put("value", "role");
        roleObj.put("text", "角色");
        JSONArray roleArray = new JSONArray();
        for (T role : roleList) {
            JSONObject roleTmp = new JSONObject();
            roleTmp.put("value", getHeader() + ((RoleVo) role).getUuid());
            roleTmp.put("text", ((RoleVo) role).getName());
            roleArray.add(roleTmp);
        }
        roleObj.put("sort", getSort());
        roleObj.put("dataList", roleArray);
        return roleObj;
    }

    @Override
    public int getSort() {
        return 4;
    }

    @Override
    public Boolean isLimit() {
        return true;
    }
}
