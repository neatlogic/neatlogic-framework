package codedriver.framework.restful.groupsearch.handler;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import codedriver.framework.common.constvalue.GroupSearch;
import codedriver.framework.dao.mapper.RoleMapper;
import codedriver.framework.dto.RoleVo;
import codedriver.framework.restful.groupsearch.core.IGroupSearchHandler;
@Service
public class RoleGroupHandler implements IGroupSearchHandler {
	@Autowired
	private RoleMapper roleMapper;
	
	@Override
	public String getName() {
		return GroupSearch.ROLE.getValue();
	}
	
	@Override
	public String getHeader() {
		return getName()+"#";
	}


	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> search(JSONObject jsonObj) {
		//总显示选项个数
		Integer total = jsonObj.getInteger("total");
		if(total == null) {
			total = 18;
		}
		List<RoleVo> roleList = new ArrayList<RoleVo>();
		RoleVo roleVo = new RoleVo();
		roleVo.setNeedPage(true);
		roleVo.setPageSize(total);
		roleVo.setCurrentPage(1);
		roleVo.setKeyword(jsonObj.getString("keyword"));
		roleList = roleMapper.searchRole(roleVo);
		return (List<T>) roleList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> reload(JSONObject jsonObj) {
		List<RoleVo> roleList = new ArrayList<RoleVo>();
		List<String> roleUuidList = new ArrayList<String>();
		for(Object value :jsonObj.getJSONArray("valueList")) {
			if(value.toString().startsWith(getHeader())){
				roleUuidList.add(value.toString().replace(getHeader(), ""));
			}
		}
		if(roleUuidList.size()>0) {
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
		for(T role:roleList) {
			JSONObject roleTmp = new JSONObject();
			roleTmp.put("value", getHeader()+((RoleVo) role).getUuid());
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
