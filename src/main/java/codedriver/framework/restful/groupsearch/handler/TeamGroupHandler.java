package codedriver.framework.restful.groupsearch.handler;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import codedriver.framework.common.constvalue.GroupSearch;
import codedriver.framework.dao.mapper.TeamMapper;
import codedriver.framework.dto.TeamVo;
import codedriver.framework.restful.groupsearch.core.IGroupSearchHandler;
@Service
public class TeamGroupHandler implements IGroupSearchHandler {
	@Autowired
	private TeamMapper teamMapper;
	
	@Override
	public String getName() {
		return GroupSearch.TEAM.getValue();
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
		List<TeamVo> teamList = new ArrayList<TeamVo>();
		TeamVo teamVo = new TeamVo();	
		teamVo.setNeedPage(true);
		teamVo.setPageSize(total);
		teamVo.setCurrentPage(1);
		teamVo.setKeyword(jsonObj.getString("keyword"));
		teamList = teamMapper.searchTeam(teamVo);
		return (List<T>) teamList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> reload(JSONObject jsonObj) {
		List<TeamVo> teamList = new ArrayList<TeamVo>();
		List<String> teamUuidList = new ArrayList<String>();
		for(Object value :jsonObj.getJSONArray("valueList")) {
			if(value.toString().startsWith(getHeader())){
				teamUuidList.add(value.toString().replace(getHeader(), ""));
			}
		}
		if(teamUuidList.size()>0) {
			teamList = teamMapper.getTeamByUuidList(teamUuidList);
		}
		return (List<T>) teamList;
	}

	@Override
	public <T> JSONObject repack(List<T> teamList) {
		JSONObject teamObj = new JSONObject();
		teamObj.put("value", "team");
		teamObj.put("text", "分组");
		JSONArray teamArray = new JSONArray();
		for(T team:teamList) {
			JSONObject teamTmp = new JSONObject();
			teamTmp.put("value", getHeader()+((TeamVo) team).getUuid());
			teamTmp.put("text", ((TeamVo) team).getName());
			teamArray.add(teamTmp);
		}
		teamObj.put("sort", getSort());
		teamObj.put("dataList", teamArray);
		return teamObj;
	}

	@Override
	public int getSort() {
		return 2;
	}

	@Override
	public Boolean isLimit() {
		return true;
	}

	@Override
	public JSONObject include(JSONObject json, List<String> includeList) {
		return json;
	}
}
